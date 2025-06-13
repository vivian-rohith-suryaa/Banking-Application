package com.viiva.handler.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.dao.account.AccountDAO;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.handler.request.RequestHandler;
import com.viiva.pojo.account.Account;
import com.viiva.pojo.request.Request;
import com.viiva.pojo.request.RequestStatus;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;
import com.viiva.wrapper.account.AccountRequest;

public class AccountHandler implements Handler<AccountRequest> {

	@Override
	public Object handle(String methodAction, AccountRequest data) throws Exception {

		switch (methodAction) {

		case "POST":

			try {
				if (!BasicUtil.isNull(data)) {
					Double balance = data.getRequest().getBalance();
					if (BasicUtil.isNull(balance) || balance <= 0) {
						throw new InputException("Balance should be greater than Zero.");
					}

					Request accountRequest = new Request();
					accountRequest.setCustomerId(data.getRequest().getCustomerId());
					accountRequest.setBranchId(data.getRequest().getBranchId());
					accountRequest.setAccountType(data.getRequest().getAccountType());
					accountRequest.setBalance(data.getRequest().getBalance());
					accountRequest.setStatus(RequestStatus.PENDING);

					RequestHandler reqHandler = new RequestHandler();

					Object result = reqHandler.handle("POST", accountRequest);

					DBUtil.commit();
					return result;
				} else {
					throw new InputException("Null Input.");
				}
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "GET":

			try {
				if (!BasicUtil.isNull(data)) {
					AccountDAO accountDao = new AccountDAO();
					if (!BasicUtil.isBlank(data.getAccount().getAccountId())) {
						long accountId = data.getAccount().getAccountId();

						Account account = accountDao.getAccountById(accountId);

						if (BasicUtil.isNull(account)) {
							throw new DBException("Fetching Account details failed.");
						}

						DBUtil.commit();

						Map<String, Object> responseData = new HashMap<String, Object>();
						responseData.put("accountId", account.getAccountId());
						responseData.put("customerId", account.getCustomerId());
						responseData.put("branchId", account.getBranchId());
						responseData.put("accountType", account.getAccountType());
						responseData.put("balance", account.getBalance());
						responseData.put("status", account.getStatus());

						return responseData;

					} else if (!BasicUtil.isBlank(data.getAccount().getCustomerId())) {
						long customerId = data.getAccount().getCustomerId();

						List<Account> accounts = accountDao.getUserAccounts(customerId);

						if (accounts.isEmpty()) {
							throw new DBException("Fetching User's account details failed.");
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
						responseData.put("accounts", accountList);
						return responseData;

					} else {
						throw new InputException("Null/Empty ID provided.");
					}
				} else {
					throw new InputException("Null Input.");
				}

			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "PUT":
			try {
				if (!BasicUtil.isNull(data)) {
					AccountDAO accountDao = new AccountDAO();
					Account account = data.getAccount();
					account.setPin(BasicUtil.encrypt(account.getPin()));
					Account updatedAccount = accountDao.updateAccountById(account);

					if (BasicUtil.isNull(updatedAccount)) {
						throw new DBException("Updating Account details failed.");
					}

					DBUtil.commit();

					Map<String, Object> responseData = new HashMap<String, Object>();

					responseData.put("accountId", updatedAccount.getAccountId());
					responseData.put("customerId", updatedAccount.getCustomerId());
					responseData.put("branchId", updatedAccount.getBranchId());
					responseData.put("accountType", updatedAccount.getAccountType());
					responseData.put("balance", updatedAccount.getBalance());
					responseData.put("status", updatedAccount.getStatus());

					return responseData;
				} else {
					throw new InputException("Null Input.");
				}
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
