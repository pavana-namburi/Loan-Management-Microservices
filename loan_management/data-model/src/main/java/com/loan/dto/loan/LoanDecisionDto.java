package com.loan.dto.loan;

import com.loan.enums.LoanDecisionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanDecisionDto {

    private Integer decisionId;
    private Integer applicationId;
    private LoanDecisionStatus loanDecisionStatus;
    private BigDecimal approvedAmount;
    private BigDecimal approvedInterestRate;
    private Integer approvedTenureMonths;
    private String decisionReason;
    private LocalDateTime decisionDate;
    private Integer decidedById;

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

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

    public Integer getApprovedTenureMonths() {
        return approvedTenureMonths;
    }

    public void setApprovedTenureMonths(Integer approvedTenureMonths) {
        this.approvedTenureMonths = approvedTenureMonths;
    }

    public Integer getDecidedById() {
        return decidedById;
    }

    public void setDecidedById(Integer decidedById) {
        this.decidedById = decidedById;
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