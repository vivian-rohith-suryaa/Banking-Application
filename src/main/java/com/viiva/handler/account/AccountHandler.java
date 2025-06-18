package com.viiva.handler.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.dao.account.AccountDAO;
import com.viiva.exceptions.AuthException;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.handler.request.RequestHandler;
import com.viiva.pojo.account.Account;
import com.viiva.pojo.account.AccountType;
import com.viiva.pojo.request.Request;
import com.viiva.pojo.request.RequestStatus;
import com.viiva.session.SessionAware;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;
import com.viiva.wrapper.account.AccountRequest;

public class AccountHandler implements Handler<AccountRequest> {

	@Override
	public Object handle(String methodAction, AccountRequest data) throws Exception {

		if (!(data instanceof SessionAware)) {
			throw new AuthException("Unauthorized: Session not found.");
		}

		long sessionUserId = data.getSessionUserId();
		long sessionBranchId = data.getSessionBranchId();

		switch (methodAction) {

		case "POST":

			try {
				if (data.getRequest().getCustomerId() != sessionUserId) {
					throw new AuthException("You can only request an account for yourself.");
				}
				if (BasicUtil.isNull(data)) {

					throw new InputException("Invalid (Null) Input.");
				}

				AccountType acctType = data.getRequest().getAccountType();
				if (acctType == null || !(acctType == AccountType.SAVINGS || acctType == AccountType.CURRENT
						|| acctType == AccountType.FIXED_DEPOSIT)) {
					throw new InputException("Invalid or unsupported account type.");
				}
				Double balance = data.getRequest().getBalance();
				if (BasicUtil.isNull(balance) || balance <= 0) {
					throw new InputException("Balance should be greater than Zero.");
				}

				Request accountRequest = new Request();
				accountRequest.setCustomerId(sessionUserId);
				accountRequest.setBranchId(sessionBranchId);
				accountRequest.setAccountType(acctType);
				accountRequest.setBalance(balance);
				accountRequest.setStatus(RequestStatus.PENDING);

				RequestHandler reqHandler = new RequestHandler();

				Object result = reqHandler.handle("POST", accountRequest);

				DBUtil.commit();
				return result;
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "GET":

			try {
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}
				AccountDAO accountDao = new AccountDAO();
				
				Account inputAccount = data.getAccount();
				
				if (!BasicUtil.isBlank(inputAccount.getAccountId())) {
					long accountId = inputAccount.getAccountId();

					Account account = accountDao.getAccountById(accountId);

					if (BasicUtil.isNull(account)) {
						throw new DBException("Account not found.");
					}
					
					if (account.getCustomerId() != sessionUserId) {
		                throw new AuthException("Access Denied: Unauthorised to fetch the account.");
		            }

					DBUtil.commit();

					Map<String, Object> responseData = new HashMap<String, Object>();
					responseData.put("message", "Account Details fetched successfully.");
					responseData.put("accountId", account.getAccountId());
					responseData.put("customerId", account.getCustomerId());
					responseData.put("branchId", account.getBranchId());
					responseData.put("accountType", account.getAccountType());
					responseData.put("balance", account.getBalance());
					responseData.put("status", account.getStatus());

					return responseData;

				} else if (!BasicUtil.isBlank(inputAccount.getCustomerId())) {
					long customerId = inputAccount.getCustomerId();
					
					if (customerId != sessionUserId) {
		                throw new AuthException("Access Denied: Unauthorised to fetch the account.");
		            }

					List<Account> accounts = accountDao.getUserAccounts(customerId);

					if (accounts.isEmpty()) {
						throw new DBException("Accounts not found for the User Id. ");
					}

					DBUtil.commit();

					List<Map<String, Object>> accountList = new ArrayList<>();
					for (Account account : accounts) {
						Map<String, Object> accountMap = new HashMap<>();
						accountMap.put("accountId", account.getAccountId());
						accountMap.put("customerId", account.getCustomerId());
						accountMap.put("branchId", account.getBranchId());
						accountMap.put("accountType", account.getAccountType());
						accountMap.put("balance", account.getBalance());
						accountMap.put("status", account.getStatus());
						accountList.add(accountMap);
					}
					Map<String, Object> responseData = new HashMap<String, Object>();
					responseData.put("message", "User's accounts fetched successfully.");
					responseData.put("accounts", accountList);
					return responseData;

				} else {
					throw new InputException("Null/Empty Account Id.");
				}

			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "PUT":
			try {
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}
				AccountDAO accountDao = new AccountDAO();
				long accountId = data.getAccount().getAccountId();
				Account existingAccount = accountDao.getAccountById(accountId);

				if (BasicUtil.isNull(existingAccount)) {
				    throw new DBException("Account not found.");
				}
				if (existingAccount.getCustomerId() != sessionUserId) {
				    throw new AuthException("Access Denied: Unauthorised to update the account.");
				}

				Account account = data.getAccount();
				account.setPin(BasicUtil.encrypt(account.getPin()));
				account.setModifiedBy(sessionUserId);
				Account updatedAccount = accountDao.updateAccountById(account);

				if (BasicUtil.isNull(updatedAccount)) {
					throw new DBException("Account not found.");
				}

				DBUtil.commit();

				Map<String, Object> responseData = new HashMap<String, Object>();
				responseData.put("message", "Account Updated Successfully.");
				responseData.put("accountId", updatedAccount.getAccountId());
				responseData.put("customerId", updatedAccount.getCustomerId());
				responseData.put("branchId", updatedAccount.getBranchId());
				responseData.put("accountType", updatedAccount.getAccountType());
				responseData.put("balance", updatedAccount.getBalance());
				responseData.put("status", updatedAccount.getStatus());

				return responseData;
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		default:
			throw new Exception("Invalid Method Action: " + methodAction);

		}
	}

	@Override
	public Class<AccountRequest> getRequestType() {
		return AccountRequest.class;
	}

}
