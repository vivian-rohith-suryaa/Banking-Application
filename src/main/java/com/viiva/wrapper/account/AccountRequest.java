package com.viiva.wrapper.account;

import java.util.Map;

import com.viiva.pojo.account.Account;
import com.viiva.pojo.request.Request;

public class AccountRequest {

	private Account account;
	private Request request;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public void setPathParams(Map<String, Object> params) {
		if (params.containsKey("account")) {
			if (this.account == null) {
				this.account = new Account();
			}
			String accountId = params.get("account").toString();
			this.account.setAccountId(Long.parseLong(accountId));

		} else if(params.containsKey("user")) {
			if (this.account == null) {
				this.account = new Account();
			}
			String customerId = params.get("user").toString();
			this.account.setCustomerId(Long.parseLong(customerId));
		}
	}

}
