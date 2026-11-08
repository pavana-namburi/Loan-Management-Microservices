package com.loan.dto.external.cibil;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EnquiryDto {

    private LocalDate enquiryDate;
    private String memberName;
    private String enquiryPurpose;
    private String enquiryAmount;

    public EnquiryDto() {
    }

    public LocalDate getEnquiryDate() {
        return enquiryDate;
    }

    public void setEnquiryDate(LocalDate enquiryDate) {
        this.enquiryDate = enquiryDate;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getEnquiryPurpose() {
        return enquiryPurpose;
    }

    public void setEnquiryPurpose(String enquiryPurpose) {
        this.enquiryPurpose = enquiryPurpose;
    }

    public String getEnquiryAmount() {
        return enquiryAmount;
    }

    public void setEnquiryAmount(String enquiryAmount) {
        this.enquiryAmount = enquiryAmount;
    }
}