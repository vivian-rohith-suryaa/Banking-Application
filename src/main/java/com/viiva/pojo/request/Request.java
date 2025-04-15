package com.viiva.pojo.request;

public class Request {

	private Long requestId;
	private Long customerId;
	private String accountType;
	private Double balance;
	private String status;
	private String remarks;
	private Long createdTime;
	private Long modifiedBy;
	private Long modifiedTime;

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
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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


}
