package com.loan.entity;

import jakarta.persistence.*;
import com.loan.enums.LoanDisbursementStatus;
import com.loan.enums.PaymentMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="loan_disbursements")
public class LoanDisbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="disbursement_id",nullable=false)
    private Integer loanDisbursementId;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="application_id",nullable = false)
    private LoanApplication loanApplication;

    @Column(name="disbursement_amount",nullable = false,precision = 12,scale = 2)
    private BigDecimal disbursementAmount;

    @Column(name="disbursement_date")
    private LocalDateTime disbursementDate;

    @Enumerated(EnumType.STRING)
    @Column(name="payment_mode",nullable = false)
    private PaymentMode paymentMode;

    @Column(name="transaction_reference",unique = true,length = 100)
    private String transactionReference;

    @Column(name="beneficiary_account_last4",length = 4)
    private String beneficiaryAccountLast4;

    @Enumerated(EnumType.STRING)
    @Column(name="status",nullable = false)
    private LoanDisbursementStatus loanDisbursementStatus=LoanDisbursementStatus.PENDING;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="processed_by")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name="created_at",insertable = false,updatable = false)
    private LocalDateTime createdAt;

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

    public LoanApplication getLoanApplication() {
        return loanApplication;
    }

    public void setLoanApplication(LoanApplication loanApplication) {
        this.loanApplication = loanApplication;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
