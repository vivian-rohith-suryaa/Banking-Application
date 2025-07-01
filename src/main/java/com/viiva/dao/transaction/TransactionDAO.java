package com.viiva.dao.transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.viiva.exceptions.DBException;
import com.viiva.pojo.transaction.Transaction;
import com.viiva.pojo.transaction.TransactionType;
import com.viiva.pojo.transaction.PaymentMode;
import com.viiva.util.DBUtil;

public class TransactionDAO {

	public List<Transaction> getTransactionsByCustomerId(long customerId, Map<String, String> queryParams) {
		int page = 1;
		int limit = 10;
		boolean usePagination = true;

		if (queryParams != null) {
			try {
				if (queryParams.containsKey("page")) {
					page = Integer.parseInt(queryParams.get("page"));
				}
				if (queryParams.containsKey("limit")) {
					limit = Integer.parseInt(queryParams.get("limit"));
					if (limit == -1) {
						usePagination = false;
					}
				}
			} catch (NumberFormatException e) {
				page = 1;
				limit = 10;
			}
		}

		int offset = (page - 1) * limit;

		StringBuilder query = new StringBuilder("SELECT * FROM transaction WHERE customer_id = ? ORDER BY transaction_time DESC");
		if (usePagination) {
			query.append(" LIMIT ? OFFSET ?");
		}

		List<Transaction> transactions = new ArrayList<>();

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query.toString())) {
			pstmt.setLong(1, customerId);

			if (usePagination) {
				pstmt.setInt(2, limit);
				pstmt.setInt(3, offset);
			}

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {
					Transaction txn = new Transaction();
					txn.setTransactionId(rs.getLong("transaction_id"));
					txn.setCustomerId(rs.getLong("customer_id"));
					txn.setAccountId(rs.getLong("account_id"));
					txn.setTransactedAccount(rs.getLong("transacted_account"));
					txn.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
					txn.setPaymentMode(PaymentMode.valueOf(rs.getString("payment_mode")));
					txn.setAmount(rs.getDouble("amount"));
					txn.setClosingBalance(rs.getDouble("closing_balance"));
					txn.setTransactionTime(rs.getLong("transaction_time"));
					txn.setExternalTransfer(rs.getBoolean("is_external_transfer"));
					txn.setExternalIfscCode(rs.getString("target_ifsc_code"));
					txn.setExternalAccountId(rs.getLong("external_account_id"));
					transactions.add(txn);
				}
			}
			return transactions;

		} catch (SQLException e) {
			throw new DBException("Failed to fetch transactions for the given customer ID." + e);
		}
	}

	public List<Transaction> getTransactionsByAccountId(long accountId, Map<String, String> queryParams) {
		int page = 1;
		int limit = 10;
		boolean usePagination = true;

		if (queryParams != null) {
			try {
				if (queryParams.containsKey("page")) {
					page = Integer.parseInt(queryParams.get("page"));
				}
				if (queryParams.containsKey("limit")) {
					limit = Integer.parseInt(queryParams.get("limit"));
					if (limit == -1) {
						usePagination = false;
					}
				}
			} catch (NumberFormatException e) {
				page = 1;
				limit = 10;
			}
		}

		int offset = (page - 1) * limit;

		StringBuilder query = new StringBuilder("SELECT * FROM transaction WHERE account_id = ? ORDER BY transaction_time DESC");
		if (usePagination) {
			query.append(" LIMIT ? OFFSET ?");
		}

		List<Transaction> transactions = new ArrayList<>();

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query.toString())) {
			pstmt.setLong(1, accountId);

			if (usePagination) {
				pstmt.setInt(2, limit);
				pstmt.setInt(3, offset);
			}

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {
					Transaction txn = new Transaction();
					txn.setTransactionId(rs.getLong("transaction_id"));
					txn.setCustomerId(rs.getLong("customer_id"));
					txn.setAccountId(rs.getLong("account_id"));
					txn.setTransactedAccount(rs.getLong("transacted_account"));
					txn.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
					txn.setPaymentMode(PaymentMode.valueOf(rs.getString("payment_mode")));
					txn.setAmount(rs.getDouble("amount"));
					txn.setExternalTransfer(rs.getBoolean("is_external_transfer"));
					txn.setExternalIfscCode(rs.getString("target_ifsc_code"));
					txn.setExternalAccountId(rs.getLong("external_account_id"));
					txn.setClosingBalance(rs.getDouble("closing_balance"));
					txn.setTransactionTime(rs.getLong("transaction_time"));
					transactions.add(txn);
				}
			}
			return transactions;

		} catch (SQLException e) {
			throw new DBException("Failed to fetch transactions for the given account ID." + e);
		}
	}


	public Map<String, Object> performTransaction(Transaction txn) {
		String query = "INSERT INTO transaction (customer_id, account_id, transacted_account, transaction_type, payment_mode, amount, closing_balance, transaction_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = DBUtil.prepareWithKeys(DBUtil.getConnection(), query)) {
			pstmt.setLong(1, txn.getCustomerId());
			pstmt.setLong(2, txn.getAccountId());
			if (txn.getTransactedAccount() != null) {
				pstmt.setLong(3, txn.getTransactedAccount());
			} else {
				pstmt.setNull(3, java.sql.Types.BIGINT);
			}
			pstmt.setString(4, txn.getTransactionType().name());
			pstmt.setString(5, txn.getPaymentMode().name());
			pstmt.setDouble(6, txn.getAmount());
			pstmt.setDouble(7, txn.getClosingBalance());
			pstmt.setLong(8, System.currentTimeMillis());

			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						long transactionId = rs.getLong(1);
						Map<String, Object> result = new HashMap<>();
						result.put("transactionId", transactionId);
						result.put("customerId", txn.getCustomerId());
						result.put("accountId", txn.getAccountId());
						result.put("transactedAccount", txn.getTransactedAccount());
						result.put("transactionType", txn.getTransactionType());
						result.put("paymentMode", txn.getPaymentMode());
						result.put("amount", txn.getAmount());
						result.put("closingBalance", txn.getClosingBalance());
						return result;
					}
				}
			}
			return null;
		} catch (SQLException e) {
			throw new DBException("Failed to save transaction", e);
		}
	}

	public Map<String, Object> performBankTransfer(Transaction txn, long receiverCustomerId,
			double receiverClosingBalance) {
		String insertSQL = "INSERT INTO transaction (transaction_reference, customer_id, account_id, transacted_account, transaction_type, payment_mode, amount, closing_balance, transaction_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement debitStmt = DBUtil.prepareWithKeys(DBUtil.getConnection(), insertSQL)) {
			debitStmt.setNull(1, java.sql.Types.BIGINT);
			debitStmt.setLong(2, txn.getCustomerId());
			debitStmt.setLong(3, txn.getAccountId());
			debitStmt.setLong(4, txn.getTransactedAccount());
			debitStmt.setString(5, txn.getTransactionType().name());
			debitStmt.setString(6, txn.getPaymentMode().name());
			debitStmt.setDouble(7, txn.getAmount());
			debitStmt.setDouble(8, txn.getClosingBalance());
			debitStmt.setLong(9, System.currentTimeMillis());

			int rows = DBUtil.executeUpdate(debitStmt);
			if (rows == 0) {
				throw new DBException("DEBIT transaction insert failed");
			}

			long debitTxnId;
			try (ResultSet rs = debitStmt.getGeneratedKeys()) {
				if (!rs.next()) {
					throw new DBException("Failed to retrieve DEBIT transaction ID");
				}
				debitTxnId = rs.getLong(1);
			}

			Long transactionReference = null;
			String refQuery = "SELECT transaction_reference FROM transaction WHERE transaction_id = ?";
			try (PreparedStatement refStmt = DBUtil.prepare(DBUtil.getConnection(), refQuery)) {
				refStmt.setLong(1, debitTxnId);
				try (ResultSet refRs = refStmt.executeQuery()) {
					if (refRs.next()) {
						transactionReference = refRs.getLong("transaction_reference");
					} else {
						throw new DBException("Transaction reference not found for DEBIT txn");
					}
				}
			}

			try (PreparedStatement creditStmt = DBUtil.prepare(DBUtil.getConnection(), insertSQL)) {
				creditStmt.setLong(1, transactionReference);
				creditStmt.setLong(2, receiverCustomerId);
				creditStmt.setLong(3, txn.getTransactedAccount());
				creditStmt.setLong(4, txn.getAccountId());
				creditStmt.setString(5, TransactionType.CREDIT.name());
				creditStmt.setString(6, PaymentMode.BANK_TRANSFER.name());
				creditStmt.setDouble(7, txn.getAmount());
				creditStmt.setDouble(8, receiverClosingBalance);
				creditStmt.setLong(9, System.currentTimeMillis());

				int creditRows = creditStmt.executeUpdate();
				if (creditRows == 0) {
					throw new DBException("CREDIT transaction insert failed");
				}
			}

			Map<String, Object> result = new HashMap<>();
			result.put("transactionId", debitTxnId);
			result.put("transactionReference", transactionReference);
			result.put("customerId", txn.getCustomerId());
			result.put("accountId", txn.getAccountId());
			result.put("transactedAccount", txn.getTransactedAccount());
			result.put("transactionType", txn.getTransactionType());
			result.put("paymentMode", txn.getPaymentMode());
			result.put("amount", txn.getAmount());
			result.put("closingBalance", txn.getClosingBalance());
			return result;

		} catch (SQLException e) {
			throw new DBException("Bank transfer failed", e);
		}

	}

	public List<Transaction> getAllTransactions(long branchId, byte role, Map<String, String> filters) {
		int page = 1;
		int limit = 10;
		boolean usePagination = true;

		if (filters != null) {
			if (filters.containsKey("page")) {
				try {
					page = Integer.parseInt(filters.get("page"));
				} catch (NumberFormatException e) {
					page = 1;
				}
				filters.remove("page");
			}
			if (filters.containsKey("limit")) {
				try {
					limit = Integer.parseInt(filters.get("limit"));
					if (limit == -1) {
						usePagination = false;
					}
				} catch (NumberFormatException e) {
					limit = 10;
				}
				filters.remove("limit");
			}
		}

		int offset = (page - 1) * limit;

		StringBuilder query = new StringBuilder(
				"SELECT t.transaction_id, t.customer_id, t.account_id, t.transacted_account, t.transaction_type, "
						+ "t.payment_mode, t.amount, t.closing_balance, t.transaction_time,t.target_ifsc_code, t.external_account_id "
						+ "FROM transaction t JOIN account a ON t.account_id = a.account_id");

		if (role == 2 || role == 3) {
			query.append(" WHERE a.branch_id = ?");
		} else {
			query.append(" WHERE 1=1");
		}

		if (filters != null) {
			if (filters.containsKey("customerId")) {
				query.append(" AND t.customer_id = ?");
			}
			if (filters.containsKey("accountId")) {
				query.append(" AND t.account_id = ?");
			}
			if (filters.containsKey("type")) {
				query.append(" AND t.transaction_type = ?");
			}
			if (filters.containsKey("paymentMode")) {
				query.append(" AND t.payment_mode = ?");
			}
		}

		query.append(" ORDER BY t.transaction_id DESC");
		if (usePagination) {
			query.append(" LIMIT ? OFFSET ?");
		}
		System.out.println("Final SQL: " + query);

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query.toString())) {
			int index = 1;

			if (role == 2 || role == 3) {
				pstmt.setLong(index++, branchId);
			}

			if (filters != null) {
				if (filters.containsKey("customerId")) {
					pstmt.setLong(index++, Long.parseLong(filters.get("customerId")));
				}
				if (filters.containsKey("accountId")) {
					pstmt.setLong(index++, Long.parseLong(filters.get("accountId")));
				}
				if (filters.containsKey("type")) {
					pstmt.setString(index++, filters.get("type"));
				}
				if (filters.containsKey("paymentMode")) {
					pstmt.setString(index++, filters.get("paymentMode"));
				}
			}

			if (usePagination) {
				pstmt.setInt(index++, limit);
				pstmt.setInt(index, offset);
			}

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				List<Transaction> list = new ArrayList<>();
				while (rs.next()) {
					Transaction t = new Transaction();
					t.setTransactionId(rs.getLong("transaction_id"));
					t.setCustomerId(rs.getLong("customer_id"));
					t.setAccountId(rs.getLong("account_id"));
					t.setTransactedAccount(rs.getLong("transacted_account"));
					t.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
					t.setExternalIfscCode(rs.getString("target_ifsc_code"));
					t.setExternalAccountId(rs.getLong("external_account_id"));
					t.setPaymentMode(PaymentMode.valueOf(rs.getString("payment_mode")));
					t.setAmount(rs.getDouble("amount"));
					t.setClosingBalance(rs.getDouble("closing_balance"));
					t.setTransactionTime(rs.getLong("transaction_time"));
					list.add(t);
				}
				return list;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error fetching transactions.", e);
		}
	}

	public Map<String, Object> performExternalBankTransfer(Transaction txn, long sourceAccountId, long createdBy) {
		String insertTransaction = "INSERT INTO transaction (customer_id, account_id, external_account_id, transaction_type, payment_mode, amount, closing_balance, transaction_time, is_external_transfer, target_ifsc_code) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = DBUtil.prepareWithKeys(DBUtil.getConnection(), insertTransaction)) {
		    pstmt.setLong(1, txn.getCustomerId());
		    pstmt.setLong(2, txn.getAccountId());
		    pstmt.setLong(3, txn.getExternalAccountId());
		    pstmt.setString(4, TransactionType.DEBIT.name());
		    pstmt.setString(5, PaymentMode.BANK_TRANSFER.name());
		    pstmt.setDouble(6, txn.getAmount());
		    pstmt.setDouble(7, txn.getClosingBalance());
		    pstmt.setLong(8, System.currentTimeMillis());
		    pstmt.setBoolean(9, true);
		    pstmt.setString(10, txn.getExternalIfscCode());

			int rows = DBUtil.executeUpdate(pstmt);
			if (rows == 0) {
				throw new DBException("Failed to insert external transaction into transaction table.");
			}

			long transactionId;
			try (ResultSet rs = pstmt.getGeneratedKeys()) {
				if (!rs.next())
					throw new DBException("Transaction ID not generated.");
				transactionId = rs.getLong(1);
			}

			String insertExternal = "INSERT INTO external_transaction (transaction_id, source_account, target_account, target_ifsc, amount, transaction_time, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";

			try (PreparedStatement extStmt = DBUtil.prepare(DBUtil.getConnection(), insertExternal)) {
				extStmt.setLong(1, transactionId);
				extStmt.setLong(2, sourceAccountId);
				extStmt.setLong(3, txn.getExternalAccountId());
				extStmt.setString(4, txn.getExternalIfscCode());
				extStmt.setDouble(5, txn.getAmount());
				extStmt.setLong(6, System.currentTimeMillis());
				extStmt.setLong(7, createdBy);

				int extRows = DBUtil.executeUpdate(extStmt);
				if (extRows == 0) {
					throw new DBException("Failed to insert into external_transaction table.");
				}
			}

			Map<String, Object> result = new HashMap<>();
			result.put("transactionId", transactionId);
			result.put("customerId", txn.getCustomerId());
			result.put("accountId", txn.getAccountId());
			result.put("externalAccountId", txn.getExternalAccountId());
			result.put("externalIfscCode", txn.getExternalIfscCode());
			result.put("amount", txn.getAmount());
			result.put("closingBalance", txn.getClosingBalance());

			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("External Bank Transfer Failed", e);
		}
	}

}
