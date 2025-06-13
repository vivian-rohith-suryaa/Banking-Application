package com.viiva.filter.auth;

import java.util.List;

public class AuthRegistry {
	
	private List<AuthRule> auth;

	public List<AuthRule> getAuth() {
		return auth;
	}

	public void setAuth(List<AuthRule> auth) {
		this.auth = auth;
	}
	
	@Override
	public String toString() {
		return "AuthRegistry [rbac =" + auth + "]";
	}

}
