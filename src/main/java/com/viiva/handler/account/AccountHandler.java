package com.viiva.handler.account;

import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.request.Request;
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
					Double balance = data.getAccount().getBalance();
					if(BasicUtil.isNull(balance) || balance<=0) {
						throw new InputException("Balance should be greater than Zero.");
					}
					
					Request accountRequest = new Request();
					accountRequest.setCustomerId(data.getAccount().getCustomerId());
					accountRequest.setBranchId(data.getAccount().getBranchId());
					accountRequest.setAccountType(data.getAccount().getAccountType());

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
