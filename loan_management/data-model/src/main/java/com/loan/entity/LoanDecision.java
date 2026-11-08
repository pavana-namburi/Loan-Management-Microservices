package com.loan.entity;

import jakarta.persistence.*;
import com.loan.enums.LoanDecisionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="loan_decisions")
public class LoanDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="decision_id",nullable = false)
    private Integer decisionId;

    @Enumerated(EnumType.STRING)
    @Column(name="decision",nullable = false)
    private LoanDecisionStatus loanDecisionStatus;

    @Column(name="approved_amount",precision = 12,scale = 2)
    private BigDecimal approvedAmount;

    @Column(name="approved_interest_rate",precision = 5,scale = 2)
    private BigDecimal approvedInterestRate;

    @Column(name="approved_tenure_months")
    private Integer approvedTenureMonths;

    @Column(name="decision_reason",columnDefinition = "TEXT")
    private String decisionReason;

    @Column(name="decision_date",nullable = false,insertable = false, updatable = false)
    private LocalDateTime decisionDate;

    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name="decided_by",nullable = true)
    private User decidedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private LoanApplication application;

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public BigDecimal getApprovedInterestRate() {
        return approvedInterestRate;
    }

    public void setApprovedInterestRate(BigDecimal approvedInterestRate) {
        this.approvedInterestRate = approvedInterestRate;
    }

    public LoanApplication getApplication() {
        return application;
    }

    public void setApplication(LoanApplication application) {
        this.application = application;
    }

    public Integer getApprovedTenureMonths() {
        return approvedTenureMonths;
    }

    public void setApprovedTenureMonths(Integer approvedTenureMonths) {
        this.approvedTenureMonths = approvedTenureMonths;
    }

    public User getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(User decidedBy) {
        this.decidedBy = decidedBy;
    }

    public LocalDateTime getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(LocalDateTime decisionDate) {
        this.decisionDate = decisionDate;
    }

    public Integer getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(Integer decisionId) {
        this.decisionId = decisionId;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }

    public LoanDecisionStatus getLoanDecisionStatus() {
        return loanDecisionStatus;
    }

    public void setLoanDecisionStatus(LoanDecisionStatus loanDecisionStatus) {
        this.loanDecisionStatus = loanDecisionStatus;
    }
}
