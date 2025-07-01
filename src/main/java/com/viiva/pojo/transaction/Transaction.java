package com.viiva.pojo.transaction;

public class Transaction {

	private Long transactionId;
	private long transactionReference;
	private Long customerId;
	private Long accountId;
	private Long transactedAccount;
	private TransactionType transactionType;
	private PaymentMode paymentMode;
	private boolean isExternalTransfer;
	private String externalIfscCode;
	private Long externalAccountId;
	private Double amount;
	private Double closingBalance;
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

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(Double closingBalance) {
		this.closingBalance = closingBalance;
	}

	public Long getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Long transactionTime) {
		this.transactionTime = transactionTime;
	}

	public long getTransactionReference() {
		return transactionReference;
	}

	public void setTransactionReference(long transactionReference) {
		this.transactionReference = transactionReference;
	}
	
	public boolean isExternalTransfer() {
		return isExternalTransfer;
	}

	public void setExternalTransfer(boolean isExternalTransfer) {
		this.isExternalTransfer = isExternalTransfer;
	}

	public String getExternalIfscCode() {
		return externalIfscCode;
	}

	public void setExternalIfscCode(String externalIfscCode) {
		this.externalIfscCode = externalIfscCode;
	}

	public Long getExternalAccountId() {
		return externalAccountId;
	}

	public void setExternalAccountId(Long externalAccountId) {
		this.externalAccountId = externalAccountId;
	}
	
	@Override
	public String toString() {
	    return "Transaction{" +
	            "transactionId=" + transactionId +
	            ", transactionReference=" + transactionReference +
	            ", customerId=" + customerId +
	            ", accountId=" + accountId +
	            ", transactedAccount=" + transactedAccount +
	            ", transactionType=" + (transactionType != null ? transactionType.name() : "null") +
	            ", paymentMode=" + (paymentMode != null ? paymentMode.name() : "null") +
	            ", isExternalTransfer=" + isExternalTransfer +
	            ", externalIfscCode='" + externalIfscCode + '\'' +
	            ", externalAccountId=" + externalAccountId +
	            ", amount=" + amount +
	            ", closingBalance=" + closingBalance +
	            ", transactionTime=" + transactionTime +
	            '}';
	}


}
