package com.viiva.wrapper.user;

import java.util.Map;

import com.viiva.pojo.customer.Customer;
import com.viiva.pojo.user.User;

public class UserWrapper {

	private Customer customer;
	private User user;

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


}
