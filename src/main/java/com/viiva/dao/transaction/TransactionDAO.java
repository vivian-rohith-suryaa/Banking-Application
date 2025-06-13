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

	public List<Transaction> getTransactionsByCustomerId(long customerId) {
		String query = "SELECT * FROM transaction WHERE customer_id = ? ORDER BY transaction_time DESC";
		List<Transaction> transactions = new ArrayList<>();

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setLong(1, customerId);

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
					transactions.add(txn);
				}
				return transactions;
			}

		} catch (SQLException e) {
			throw new DBException("Failed to fetch transactions for the given customer ID." + e);
		}
	}

	public List<Transaction> getTransactionsByAccountId(long accountId) {
		String query = "SELECT * FROM transaction WHERE account_id = ? ORDER BY transaction_time DESC";
		List<Transaction> transactions = new ArrayList<>();

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setLong(1, accountId);

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
					transactions.add(txn);
				}
				return transactions;
			}

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
						result.put("closing_balance", txn.getClosingBalance());
						return result;
					}
				}
			}
			return null;
		} catch (SQLException e) {
			System.out.println(e);
			throw new DBException("Failed to save transaction", e);
		}
	}

	public Map<String, Object> performBankTransfer(Transaction txn, long receiverCustomerId, double receiverClosingBalance) {
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
}
