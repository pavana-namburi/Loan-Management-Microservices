package com.loan.entity;

import jakarta.persistence.*;
import com.loan.enums.LoanType;
import com.loan.enums.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    @Column(name = "application_number",
            nullable = false, unique = true, length = 50)
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    @Column(name = "requested_amount",
            nullable = false, precision = 12, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "tenure_months", nullable =   false)
    private Integer tenureMonths;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "loan_purpose", columnDefinition = "TEXT")
    private String loanPurpose;

    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(
            mappedBy = "application",
            cascade = CascadeType.PERSIST
    )
    private List<CibilDetails> cibilDetailsList;

    @OneToMany(
            mappedBy = "application",
            cascade = CascadeType.PERSIST
    )
    private List<LoanDecision> loanDecisionList;

    @OneToMany(mappedBy = "loanApplication",
            cascade = CascadeType.PERSIST )
    private List<LoanDisbursement> loanDisbursementsList=new ArrayList<>();

    public List<CibilDetails> getCibilDetailsList() {
        return cibilDetailsList;
    }

    public void setCibilDetailsList(List<CibilDetails> cibilDetailsList) {
        this.cibilDetailsList = cibilDetailsList;
    }

    public List<LoanDecision> getLoanDecisionList() {
        return loanDecisionList;
    }

    public void setLoanDecisionList(List<LoanDecision> loanDecisionList) {
        this.loanDecisionList = loanDecisionList;
    }

    public List<LoanDisbursement> getLoanDisbursementsList() {
        return loanDisbursementsList;
    }

    public void setLoanDisbursementsList(List<LoanDisbursement> loanDisbursementsList) {
        this.loanDisbursementsList = loanDisbursementsList;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }
}

