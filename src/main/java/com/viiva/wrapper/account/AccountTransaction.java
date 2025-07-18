package com.viiva.wrapper.account;

import java.util.Map;

import com.viiva.pojo.account.Account;
import com.viiva.pojo.transaction.Transaction;
import com.viiva.session.SessionAware;

public class AccountTransaction implements SessionAware{

	private Account account;
	private Transaction transaction;
	private String password;
	private Map<String, Object> sessionAttributes;
	private Map<String, String> queryParams;

	public Map<String, String> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map<String, String> queryParams) {
		this.queryParams = queryParams;
	}

	@Override
	public void setSessionAttributes(Map<String, Object> sessionAttributes) {
		this.sessionAttributes = sessionAttributes;
	}
	
	@Override
	public Map<String, Object> getSessionAttributes() {
	    return this.sessionAttributes;
	}


	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	
	public void setPathParams(Map<String, Object> params) {
		if (params.containsKey("account")) {
			if (this.transaction == null) {
				this.transaction = new Transaction();
			}
			String accountId = params.get("account").toString();
			this.transaction.setAccountId(Long.parseLong(accountId));

		} else if(params.containsKey("user")) {
			if (this.transaction == null) {
				this.transaction = new Transaction();
			}
			String customerId = params.get("user").toString();
			this.transaction.setCustomerId(Long.parseLong(customerId));
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
