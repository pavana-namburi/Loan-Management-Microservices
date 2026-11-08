package com.loan.dto.external.cibil;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReportResponseDto {

    private String reportId;
    private Instant generatedOn;
    private CustomerDto customer;
    private CibilScoreDto cibilScore;
    private List<AccountDto> accounts;
    private List<EnquiryDto> enquiries;
    private List<String> remarks;

    public ReportResponseDto() {
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public Instant getGeneratedOn() {
        return generatedOn;
    }

    public void setGeneratedOn(Instant generatedOn) {
        this.generatedOn = generatedOn;
    }

    public CustomerDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDto customer) {
        this.customer = customer;
    }

    public CibilScoreDto getCibilScore() {
        return cibilScore;
    }

    public void setCibilScore(CibilScoreDto cibilScore) {
        this.cibilScore = cibilScore;
    }

    public List<AccountDto> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDto> accounts) {
        this.accounts = accounts;
    }

    public List<EnquiryDto> getEnquiries() {
        return enquiries;
    }

    public void setEnquiries(List<EnquiryDto> enquiries) {
        this.enquiries = enquiries;
    }

    public List<String> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<String> remarks) {
        this.remarks = remarks;
    }
}