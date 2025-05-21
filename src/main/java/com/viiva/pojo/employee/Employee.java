package com.viiva.pojo.employee;

import java.util.Map;

import com.viiva.pojo.user.User;

public class Employee extends User{

	private Long employeeId;
	private Long branchId;
	
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
