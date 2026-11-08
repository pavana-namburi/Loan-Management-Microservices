package com.loan.entity;

import jakarta.persistence.*;
import com.loan.enums.VerificationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "cibil_details")
public class CibilDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cibil_id")
    private Integer cibilId;

    @Column(name = "credit_score")
    private Short creditScore;

    @Column(name = "report_reference", length = 100)
    private String reportReference;

    @Column(name = "checked_date", nullable = false,
            insertable = false, updatable = false)
    private LocalDateTime checkedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus =
            VerificationStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private LoanApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_by")
    private User checkedBy;

    public CibilDetails() {
    }

    public LoanApplication getApplication() {
        return application;
    }

    public void setApplication(LoanApplication application) {
        this.application = application;
    }

    public User getCheckedBy() {
        return checkedBy;
    }

    public void setCheckedBy(User checkedBy) {
        this.checkedBy = checkedBy;
    }

    public LocalDateTime getCheckedDate() {
        return checkedDate;
    }

    public void setCheckedDate(LocalDateTime checkedDate) {
        this.checkedDate = checkedDate;
    }

    public Integer getCibilId() {
        return cibilId;
    }

    public void setCibilId(Integer cibilId) {
        this.cibilId = cibilId;
    }

    public Short getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Short creditScore) {
        this.creditScore = creditScore;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getReportReference() {
        return reportReference;
    }

    public void setReportReference(String reportReference) {
        this.reportReference = reportReference;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}