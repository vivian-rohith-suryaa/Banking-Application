package com.viiva.pojo.branch;

import java.util.Map;

import com.viiva.session.SessionAware;

public class Branch implements SessionAware{

	private Long branchId;
	private Long managerId;
	private String branchName;
	private String ifscCode;
	private String locality;
	private String district;
	private String state;
	private Long createdTime;
	private Long modifiedTime;
	private Long modifiedBy;
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

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}

	public Long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	public void setPathParams(Map<String, Object> params) {
	    if (params.containsKey("branch")) {
	        String branchId = params.get("branch").toString();
	        this.setBranchId(Long.parseLong(branchId));
	    }
	}

}
