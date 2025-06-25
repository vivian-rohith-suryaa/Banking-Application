package com.viiva.wrapper.account;

import java.util.Map;

import com.viiva.pojo.account.Account;
import com.viiva.pojo.request.Request;
import com.viiva.session.SessionAware;

public class AccountRequest implements SessionAware{

	private Account account;
	private Request request;
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
	
	@Override
	public String toString() {
	    return "AccountRequest{" +
	            "account=" + account.toString() +
	            ", request=" + request +
	            ", sessionAttributes=" + (sessionAttributes != null ? sessionAttributes.keySet() : null) +
	            ", queryParams=" + queryParams +
	            '}';
	}


}
