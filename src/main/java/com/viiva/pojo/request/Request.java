package com.viiva.pojo.request;

import java.util.Map;
import com.viiva.pojo.account.AccountType;
import com.viiva.session.SessionAware;

public class Request implements SessionAware{

	private Long requestId;
	private Long customerId;
	private Long branchId;
	private AccountType accountType;
	private Double balance;
	private RequestStatus status;
	private String remarks;
	private Long createdTime;
	private Long modifiedBy;
	private Long modifiedTime;
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

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public RequestStatus getStatus() {
		return status;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}
	
	public void setPathParams(Map<String, Object> params) {
	    if (params.containsKey("request")) {
	        String requestId = params.get("request").toString();
	        this.setRequestId(Long.parseLong(requestId));
	    }
	}
	
	@Override
	public String toString() {
	    return "Request{" +
	            "requestId=" + requestId +
	            ", customerId=" + customerId +
	            ", branchId=" + branchId +
	            ", accountType=" + accountType +
	            ", balance=" + balance +
	            ", status=" + status +
	            ", remarks='" + remarks + '\'' +
	            ", createdTime=" + createdTime +
	            ", modifiedBy=" + modifiedBy +
	            ", modifiedTime=" + modifiedTime +
	            '}';
	}


}
