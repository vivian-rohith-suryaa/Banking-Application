package com.viiva.filter.auth;

import java.util.List;

public class AuthRule {

	private String path;
	private String method;
	private List<Integer> roles;

	public List<Integer> getRoles() {
		return roles;
	}

	public void setRoles(List<Integer> roles) {
		this.roles = roles;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public String toString() {
		return "AuthRule [path=" + path + ", method=" + method + ", roles=" + roles + "]";
	}

}

