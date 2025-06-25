package com.viiva.pojo.employee;

import java.util.Map;
import com.viiva.pojo.user.User;
import com.viiva.session.SessionAware;

public class Employee extends User implements SessionAware{

	private Long employeeId;
	private Long branchId;
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
	
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getBranchId() {
		return branchId;
	}
	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public void setPathParams(Map<String, Object> params) {
	    if (params.containsKey("employee")) {
	        String employeeId = params.get("employee").toString();
	        this.setEmployeeId(Long.parseLong(employeeId));
	    }
	}
}
