package com.viiva.pojo.transaction;

public class Transaction {

	private Long transactionId;
	private Long customerId;
	private Long accountId;
	private Long transactedAccount;
	private String transactionType;
	private String paymentMode;
	private Double amount;
	private Long closingBalance;
	private Long transactionTime;

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getTransactedAccount() {
		return transactedAccount;
	}

	public void setTransactedAccount(Long transactedAccount) {
		this.transactedAccount = transactedAccount;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(Long closingBalance) {
		this.closingBalance = closingBalance;
	}

	public Long getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Long transactionTime) {
		this.transactionTime = transactionTime;
	}

}
