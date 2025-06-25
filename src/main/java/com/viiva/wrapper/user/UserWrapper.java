package com.viiva.wrapper.user;

import java.util.Map;
import com.viiva.pojo.customer.Customer;
import com.viiva.pojo.user.User;
import com.viiva.session.SessionAware;

public class UserWrapper implements SessionAware{

	private Customer customer;
	private User user;
	private Map<String, Object> sessionAttributes;
	private Map<String, String> queryParams;

	@Override
	public void setSessionAttributes(Map<String, Object> sessionAttributes) {
		this.sessionAttributes = sessionAttributes;
	}
	
	@Override
	public Map<String, Object> getSessionAttributes() {
	    return this.sessionAttributes;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public void setPathParams(Map<String, Object> params) {
	    if (params.containsKey("user")) {
	        if (this.user == null) {
	            this.user = new User();
	        }
	        String userId = params.get("user").toString();
	        this.user.setUserId(Long.parseLong(userId));
	    }
	}

	public Map<String, String> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map<String, String> queryParams) {
		this.queryParams = queryParams;
	}


}
