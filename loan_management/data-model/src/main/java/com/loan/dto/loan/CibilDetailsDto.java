package com.loan.dto.loan;

import com.loan.enums.VerificationStatus;
import java.time.LocalDateTime;

public class CibilDetailsDto {

    private Integer cibilId;
    private Integer applicationId;
    private Short creditScore;
    private String reportReference;
    private LocalDateTime checkedDate;
    private VerificationStatus verificationStatus;
    private String remarks;
    private Integer checkedById;

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getCheckedById() {
        return checkedById;
    }

    public void setCheckedById(Integer checkedById) {
        this.checkedById = checkedById;
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