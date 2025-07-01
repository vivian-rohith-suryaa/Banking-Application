package com.viiva.handler.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.dao.account.AccountDAO;
import com.viiva.dao.transaction.TransactionDAO;
import com.viiva.exceptions.AuthException;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.account.Account;
import com.viiva.pojo.transaction.PaymentMode;
import com.viiva.pojo.transaction.Transaction;
import com.viiva.pojo.transaction.TransactionType;
import com.viiva.session.SessionAware;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;
import com.viiva.wrapper.account.AccountTransaction;

public class TransactionHandler implements Handler<AccountTransaction> {

	@Override
	public Object handle(String methodAction, AccountTransaction data) throws Exception {

		if (!(data instanceof SessionAware)) {
			throw new AuthException("Unauthorized: Session not found.");
		}

		long sessionUserId = data.getSessionUserId();

		switch (methodAction) {

		case "GET": {
			try {
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}

				System.out.println("came here");

				byte sessionRole = data.getSessionRole();
				Long sessionBranchId = data.getSessionBranchId();

				TransactionDAO transactionDao = new TransactionDAO();
				Transaction transaction = data.getTransaction();

				if (transaction == null) {
					transaction = new Transaction();
				}

				List<Transaction> transactions;

				if (!BasicUtil.isBlank(transaction.getAccountId())) {
					long accountId = transaction.getAccountId();

					AccountDAO accountDao = new AccountDAO();
					Account acc = accountDao.getAccountById(accountId);

					if (acc == null) {
						throw new DBException("Account not found.");
					}

					if (sessionRole < 2 && acc.getCustomerId() != sessionUserId) {
						throw new AuthException("Access Denied: Unauthorized to fetch transactions.");
					}

					transactions = transactionDao.getTransactionsByAccountId(accountId, data.getQueryParams());
				} else if (!BasicUtil.isBlank(transaction.getCustomerId())) {
					long customerId = transaction.getCustomerId();

					if (sessionRole < 2 && customerId != sessionUserId) {
						throw new AuthException("Access Denied: Unauthorized to fetch transactions.");
					}

					transactions = transactionDao.getTransactionsByCustomerId(customerId, data.getQueryParams());
				} else if (sessionRole >= 2) {
					Map<String, String> filters = data.getQueryParams();
					transactions = transactionDao.getAllTransactions(sessionBranchId, sessionRole, filters);
				} else {
					throw new InputException("Null/Empty transaction fields.");
				}

				if (transactions == null || transactions.isEmpty()) {
					throw new DBException("No transactions found.");
				}

				DBUtil.commit();

				List<Map<String, Object>> transactionList = new ArrayList<>();
				for (Transaction t : transactions) {
					Map<String, Object> map = new HashMap<>();
					map.put("transactionId", t.getTransactionId());
					map.put("customerId", t.getCustomerId());
					map.put("accountId", t.getAccountId());
					map.put("transactedAccount", t.getTransactedAccount());
					map.put("transactionType", t.getTransactionType());
					map.put("paymentMode", t.getPaymentMode());
					map.put("isExternalTransfer", t.isExternalTransfer());
					map.put("externalIfscCode", t.getExternalIfscCode());
					map.put("externalAccountId", t.getExternalAccountId());
					map.put("amount", t.getAmount());
					map.put("closingBalance", t.getClosingBalance());
					map.put("transactionTime", t.getTransactionTime());
					
					transactionList.add(map);
				}

				Map<String, Object> response = new HashMap<>();
				response.put("message", "Transactions fetched successfully.");
				response.put("transactions", transactionList);
				return response;

			} catch (Exception e) {
				DBUtil.rollback();
				throw e;
			}
		}

		case "POST": {
			try {
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}
				Account inputAccount = data.getAccount();
				Transaction transaction = data.getTransaction();

				double transactedAmount = transaction.getAmount();

				if (BasicUtil.isNull(transactedAmount) || transactedAmount <= 0) {
					throw new InputException("Invalid Transaction Amount.");
				}

				AccountDAO accountDao = new AccountDAO();
				TransactionDAO transactionDao = new TransactionDAO();

				long sourceId = inputAccount.getAccountId();
				Long targetId = transaction.getTransactedAccount();

				Account source = accountDao.getAccountById(sourceId);
				if (source == null) {
					throw new DBException("Source account not found.");
				}
				long customerId = source.getCustomerId();

				if (customerId != sessionUserId) {
					throw new AuthException("Access Denied: Unauthorised to perform the transaction.");
				}

				if (targetId != null && sourceId == targetId.longValue()) {
				    throw new InputException("Invalid Transaction Operation.");
				}


				Account target = null;

				if (!BasicUtil.isNull(targetId)) {
					if (sourceId > targetId) {
						source = accountDao.getAccountById(sourceId);
						target = accountDao.getAccountById(targetId);
					} else {
						target = accountDao.getAccountById(targetId);
						source = accountDao.getAccountById(sourceId);
					}
				}

				// Validate PIN
				System.out.println(inputAccount.getPin());
				System.out.println(source.getPin());

				if (!BasicUtil.checkPassword(inputAccount.getPin(), source.getPin())) {
					throw new InputException("Incorrect PIN.");
				}

				PaymentMode mode = transaction.getPaymentMode();
				TransactionType type = transaction.getTransactionType();

				if ((mode == PaymentMode.WITHDRAWAL || mode == PaymentMode.DEPOSIT)
						&& !BasicUtil.isBlank(transaction.getTransactedAccount())) {
					throw new InputException(mode + " should not have a transacted account.");

				}

				if (mode == PaymentMode.SELF_TRANSFER) {
					if (BasicUtil.isBlank(transaction.getTransactedAccount())) {
						throw new InputException(mode + " requires a transacted account.");
					}
					target = accountDao.getAccountById(transaction.getTransactedAccount());
					if (BasicUtil.isNull(target)) {
						throw new InputException("Target account not found.");
					}
					if (!target.getCustomerId().equals(source.getCustomerId())) {
						throw new InputException("Self-transfer must be between accounts of the same customer.");
					}
				}

				if (mode == PaymentMode.BANK_TRANSFER) {
					if (transaction.isExternalTransfer()) {
						if (BasicUtil.isBlank(transaction.getExternalAccountId()) || BasicUtil.isBlank(transaction.getExternalIfscCode())) {
							throw new InputException("Invalid External Bank Details.");
						}
					} else {
						if (BasicUtil.isNull(target)) {
							throw new InputException("Transacted Account not found.");
						}
						if (BasicUtil.isBlank(transaction.getTransactedAccount())) {
							throw new InputException(mode + " requires a transacted account.");
						}
					}

				}

				if (type == TransactionType.CREDIT) {
					switch (mode) {
					case DEPOSIT:

						setDeposit(source, transaction, transactedAmount);
						break;

					case SELF_TRANSFER:

						if (source.getBalance() < transactedAmount) {
							throw new InputException("Insufficient balance.");
						}

						setSelfTransfer(source, target, transaction, transactedAmount);
						break;

					default:
						throw new InputException("Unsupported credit mode.");
					}
				} else if (type == TransactionType.DEBIT) {
					switch (mode) {
					case BANK_TRANSFER:
						if (source.getBalance() < transactedAmount) {
							throw new InputException("Insufficient balance.");
						}

						if (transaction.isExternalTransfer()) {
							source.setBalance(source.getBalance() - transactedAmount);
							transaction.setAccountId(source.getAccountId());
							transaction.setCustomerId(source.getCustomerId());
							transaction.setClosingBalance(source.getBalance());
						} else {
							setDebitTransfer(source, target, transaction, transactedAmount);
						}

						break;

					case WITHDRAWAL:
						if (source.getBalance() < transactedAmount) {
							throw new InputException("Insufficient balance.");
						}

						setWithdrawal(source, transaction, transactedAmount);
						break;

					default:
						throw new InputException("Unsupported debit mode.");
					}

				} else {
					throw new InputException("Invalid transaction type.");
				}

				Map<String, Object> result;
				if (type == TransactionType.DEBIT && mode == PaymentMode.BANK_TRANSFER) {
					if (transaction.isExternalTransfer()) {
						result = transactionDao.performExternalBankTransfer(transaction, source.getAccountId(),
								sessionUserId);
					} else {
						result = transactionDao.performBankTransfer(transaction, target.getCustomerId(),
								target.getBalance());
					}
				} else {
					result = transactionDao.performTransaction(transaction);
				}

				if (BasicUtil.isNull(result)) {
					throw new DBException("Transaction failed.");
				}
				if (!accountDao.updateBalance(source)) {
					throw new DBException("Transaction failed.");
				}

				if (!BasicUtil.isNull(target)) {
					if (!accountDao.updateBalance(target)) {
						throw new DBException("Transaction failed.");
					}
				}

				DBUtil.commit();
				result.put("message", "Transaction Successful.");
				return result;
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}
		}

		default:
			throw new InputException("Invalid method action: " + methodAction);
		}

	}

	private void setDebitTransfer(Account source, Account target, Transaction transaction, double transactedAmount) {
		source.setBalance(source.getBalance() - transactedAmount);
		target.setBalance(target.getBalance() + transactedAmount);
		transaction.setAccountId(source.getAccountId());
		transaction.setCustomerId(source.getCustomerId());
		transaction.setClosingBalance(source.getBalance());

	}

	private void setDeposit(Account source, Transaction transaction, double transactedAmount) {
		source.setBalance(source.getBalance() + transactedAmount);
		transaction.setAccountId(source.getAccountId());
		transaction.setCustomerId(source.getCustomerId());
		transaction.setClosingBalance(source.getBalance());
	}

	private void setSelfTransfer(Account source, Account target, Transaction transaction, double transactedAmount) {
		source.setBalance(source.getBalance() - transactedAmount);
		target.setBalance(target.getBalance() + transactedAmount);
		transaction.setAccountId(source.getAccountId());
		transaction.setCustomerId(source.getCustomerId());
		transaction.setClosingBalance(source.getBalance());
	}

	private void setWithdrawal(Account source, Transaction transaction, double transactedAmount) {
		source.setBalance(source.getBalance() - transactedAmount);
		transaction.setAccountId(source.getAccountId());
		transaction.setCustomerId(source.getCustomerId());
		transaction.setClosingBalance(source.getBalance());
	}

	@Override
	public Class<AccountTransaction> getRequestType() {
		return AccountTransaction.class;
	}

}
