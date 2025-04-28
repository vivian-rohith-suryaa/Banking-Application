package com.viiva.wrapper.signup;

import com.viiva.pojo.customer.Customer;
import com.viiva.pojo.user.User;

public class SignupRequest {

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

}
