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

		byte sessionRole = data.getSessionRole();
		long sessionUserId = data.getSessionUserId();
		long sessionBranchId = data.getSessionBranchId();

		switch (methodAction) {

		case "GET": {
			try {
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}
				
				Transaction transaction = data.getTransaction();
				TransactionDAO transactionDao = new TransactionDAO();
				
				List<Transaction> transactions;
				
				if (!BasicUtil.isBlank(transaction.getAccountId())) {
					long accountId = transaction.getAccountId();
					AccountDAO accountDao = new AccountDAO();
		            Account acc = accountDao.getAccountById(accountId);

		            if (acc == null) {
		                throw new DBException("Account not found.");
		            }
		            
		            if (acc.getCustomerId() != sessionUserId) {
		                throw new AuthException("Access Denied: Unauthorised to fetch the transaction.");
		            }
		            
					transactions = transactionDao.getTransactionsByAccountId(accountId);
					
				} else if (!BasicUtil.isBlank(transaction.getCustomerId())) {
					long customerId = transaction.getCustomerId();
					
					if (customerId != sessionUserId) {
		                throw new AuthException("Access Denied: Unauthorised to fetch the transaction.");
		            }
					
					transactions = transactionDao.getTransactionsByCustomerId(customerId);

				} else {
					throw new InputException("Null/Empty Transaction Id.");
				}
				if (BasicUtil.isNull(transactions) || transactions.isEmpty()) {
					throw new DBException("Transaction not found.");
				}

				DBUtil.commit();

				List<Map<String, Object>> transactionList = new ArrayList<>();
				for (Transaction individualTransaction : transactions) {
					Map<String, Object> transactionMap = new HashMap<>();
					transactionMap.put("transactionId", individualTransaction.getTransactionId());
					transactionMap.put("customerId", individualTransaction.getCustomerId());
					transactionMap.put("accountId", individualTransaction.getAccountId());
					transactionMap.put("transactedAccount", individualTransaction.getTransactedAccount());
					transactionMap.put("trasnactionType", individualTransaction.getTransactionType());
					transactionMap.put("paymentMode", individualTransaction.getPaymentMode());
					transactionMap.put("amount", individualTransaction.getAmount());
					transactionMap.put("closingBalance", individualTransaction.getClosingBalance());
					transactionMap.put("transactionTime", individualTransaction.getTransactionTime());
					transactionList.add(transactionMap);
				}

				Map<String, Object> responseData = new HashMap<String, Object>();
				responseData.put("message", "Transactions fetched successfully.");
				responseData.put("transactions", transactionList);
				return responseData;

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
				long targetId = transaction.getTransactedAccount();
				
				if (sourceId != sessionUserId) {
				    throw new AuthException("Access Denied: Unauthorised to fetch the transaction.");
				}

				Account source;
				Account target = null;

//				if (BasicUtil.isNull(targetId) && sourceId > targetId) {
//					target = accountDao.getAccountById(targetId);
//					source = accountDao.getAccountById(sourceId);
//				} else {
				
				source = accountDao.getAccountById(sourceId);
				if (!BasicUtil.isBlank(targetId)) {
				    target = accountDao.getAccountById(targetId);
				} else {
					source = accountDao.getAccountById(sourceId);
					if (!BasicUtil.isNull(targetId)) {
						target = accountDao.getAccountById(targetId);
					}
				}

				if (source == null) {
					throw new DBException("Source account not found.");
				}

				// Validate PIN
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
					if (BasicUtil.isNull(target) || !target.getCustomerId().equals(source.getCustomerId())) {
						throw new InputException("Self-transfer must be between accounts of the same customer.");
					}
				}

				if (mode == PaymentMode.BANK_TRANSFER) {
					if (BasicUtil.isBlank(transaction.getTransactedAccount())) {
						throw new InputException(mode + " requires a transacted account.");
					}
					if (BasicUtil.isNull(target)) {
						throw new InputException("Transacted Account not found.");
					}

				}

				if (type == TransactionType.CREDIT) {
					switch (mode) {
					case DEPOSIT:

						setDeposit(source, transaction, transactedAmount);
						break;

					case SELF_TRANSFER:

						if (BasicUtil.isNull(target)) {
							throw new InputException("Target account not found.");
						}

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

						setDebitTransfer(source, target, transaction, transactedAmount);
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
					result = transactionDao.performBankTransfer(transaction, target.getCustomerId(),
							target.getBalance());
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
