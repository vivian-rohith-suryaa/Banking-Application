package com.viiva.wrapper.account;

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

}
