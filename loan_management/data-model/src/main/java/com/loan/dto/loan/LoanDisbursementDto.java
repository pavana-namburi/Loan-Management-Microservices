package com.loan.dto.loan;

import com.loan.enums.LoanDisbursementStatus;
import com.loan.enums.PaymentMode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanDisbursementDto {

    private Integer loanDisbursementId;
    private Integer applicationId;
    private BigDecimal disbursementAmount;
    private LocalDateTime disbursementDate;
    private PaymentMode paymentMode;
    private String transactionReference;
    private String beneficiaryAccountLast4;
    private LoanDisbursementStatus loanDisbursementStatus;
    private Integer processedById;
    private String remarks;
    private LocalDateTime createdAt;

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getBeneficiaryAccountLast4() {
        return beneficiaryAccountLast4;
    }

    public void setBeneficiaryAccountLast4(String beneficiaryAccountLast4) {
        this.beneficiaryAccountLast4 = beneficiaryAccountLast4;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getDisbursementAmount() {
        return disbursementAmount;
    }

    public void setDisbursementAmount(BigDecimal disbursementAmount) {
        this.disbursementAmount = disbursementAmount;
    }

    public LocalDateTime getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(LocalDateTime disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public Integer getLoanDisbursementId() {
        return loanDisbursementId;
    }

    public void setLoanDisbursementId(Integer loanDisbursementId) {
        this.loanDisbursementId = loanDisbursementId;
    }

    public LoanDisbursementStatus getLoanDisbursementStatus() {
        return loanDisbursementStatus;
    }

    public void setLoanDisbursementStatus(LoanDisbursementStatus loanDisbursementStatus) {
        this.loanDisbursementStatus = loanDisbursementStatus;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Integer getProcessedById() {
        return processedById;
    }

    public void setProcessedById(Integer processedById) {
        this.processedById = processedById;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }
}