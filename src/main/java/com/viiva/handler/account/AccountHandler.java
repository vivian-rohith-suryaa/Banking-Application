package com.viiva.handler.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.dao.account.AccountDAO;
import com.viiva.dao.request.RequestDAO;
import com.viiva.exceptions.AuthException;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.account.Account;
import com.viiva.pojo.account.AccountType;
import com.viiva.pojo.request.Request;
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

		switch (methodAction) {

		case "POST":

			try {
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}
				Request request = data.getRequest();
				if (BasicUtil.isNull(request)) {
					throw new InputException("Missing request data.");
				}
				byte sessionRole = data.getSessionRole();
				if (sessionRole == 1 && request.getCustomerId() != sessionUserId) {
					throw new AuthException("Access Denied: Unauthorised to request an account.");
				}
				
				if (sessionRole >= 2 && request.getCustomerId() == sessionUserId) {
		            throw new AuthException("Access Denied: Unauthorised to create accounts for user role.");
		        }

				AccountType acctType = request.getAccountType();
				if (BasicUtil.isNull(acctType) || !(acctType == AccountType.SAVINGS || acctType == AccountType.CURRENT
						|| acctType == AccountType.FIXED_DEPOSIT)) {
					throw new InputException("Invalid or unsupported account type.");
				}
				
				Double balance = data.getRequest().getBalance();
				if (BasicUtil.isNull(balance) || balance < 0) {
					throw new InputException("Balance should be greater than Zero.");
				}
				
				request.setModifiedBy(sessionUserId);
				
				Map<String, Object> responseData = new HashMap<String, Object>();
				
				if(sessionRole >=2) {
					AccountDAO accountDao = new AccountDAO();
					Map<String, Object> result = accountDao.createAccount(request);

					if (BasicUtil.isNull(result)) {
						throw new DBException("Couldn't create account directly.");
					}

					DBUtil.commit();

					responseData.put("message", "Account created successfully.");
					responseData.put("accountId", result.get("accountId"));
					responseData.put("customerId", result.get("customerId"));
					return responseData;
					
				}
				else {
					RequestDAO requestDao = new RequestDAO();
					Map<String, Object> result = requestDao.createRequest(request);
	
					if (BasicUtil.isNull(result)) {
						throw new DBException("Couldn't request for an account");
					}
	
					DBUtil.commit();
					
					responseData.put("message", "Request created for an account.");
					responseData.put("requestID", result.get("requestId"));
					responseData.put("customerId", result.get("customerId"));
					responseData.put("status", result.get("status"));
	
					return responseData;
				}

			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "GET":
			try {
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}

				byte sessionRole = data.getSessionRole();
				Long sessionBranchId = data.getSessionBranchId();

				AccountDAO accountDao = new AccountDAO();

				Account inputAccount = data.getAccount();

				if (inputAccount != null) {
					if (!BasicUtil.isBlank(inputAccount.getAccountId())) {
						long accountId = inputAccount.getAccountId();
						Account account = accountDao.getAccountById(accountId);

						if (BasicUtil.isNull(account)) {
							throw new DBException("Account not found.");
						}

						if (sessionRole < 2 && account.getCustomerId() != sessionUserId) {
							throw new AuthException("Access Denied: Unauthorized to fetch this account.");
						}

						DBUtil.commit();

						Map<String, Object> response = new HashMap<>();
						response.put("message", "Account Details fetched successfully.");
						response.put("accountId", account.getAccountId());
						response.put("customerId", account.getCustomerId());
						response.put("branchId", account.getBranchId());
						response.put("accountType", account.getAccountType());
						response.put("balance", account.getBalance());
						response.put("status", account.getStatus());
						return response;
					}

					if (!BasicUtil.isBlank(inputAccount.getCustomerId())) {
						long customerId = inputAccount.getCustomerId();

						if (sessionRole < 2 && customerId != sessionUserId) {
							throw new AuthException("Access Denied: Unauthorized to fetch accounts.");
						}

						List<Account> accounts = accountDao.getUserAccounts(customerId);
						if (accounts.isEmpty()) {
							throw new DBException("Accounts not found for this customer.");
						}

						DBUtil.commit();

						List<Map<String, Object>> accountList = new ArrayList<>();
						for (Account acc : accounts) {
							Map<String, Object> map = new HashMap<>();
							map.put("accountId", acc.getAccountId());
							map.put("customerId", acc.getCustomerId());
							map.put("branchId", acc.getBranchId());
							map.put("accountType", acc.getAccountType());
							map.put("balance", acc.getBalance());
							map.put("status", acc.getStatus());
							accountList.add(map);
						}

						Map<String, Object> response = new HashMap<>();
						response.put("message", "Customer's accounts fetched successfully.");
						response.put("accounts", accountList);
						return response;
					}
				}

				if (sessionRole < 2) {
					throw new AuthException("Access Denied: Unauthorized to fetch all accounts.");
				}

				Map<String, String> queryParams = data.getQueryParams();
				List<Account> accounts = accountDao.getAllAccounts(sessionBranchId, sessionRole, queryParams);
				if (accounts.isEmpty()) {
					throw new DBException("No accounts found.");
				}

				DBUtil.commit();

				List<Map<String, Object>> accountList = new ArrayList<>();
				for (Account acc : accounts) {
					Map<String, Object> accountMap = new HashMap<>();
					accountMap.put("accountId", acc.getAccountId());
					accountMap.put("customerId", acc.getCustomerId());
					accountMap.put("branchId", acc.getBranchId());
					accountMap.put("accountType", acc.getAccountType());
					accountMap.put("balance", acc.getBalance());
					accountMap.put("status", acc.getStatus());
					accountList.add(accountMap);
				}

				Map<String, Object> response = new HashMap<>();
				response.put("message", "Accounts fetched successfully.");
				response.put("accounts", accountList);
				return response;

			} catch (Exception e) {
				DBUtil.rollback();
				throw e;
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
