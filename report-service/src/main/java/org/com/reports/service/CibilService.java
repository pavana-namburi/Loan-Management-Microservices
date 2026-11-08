package org.com.reports.service;

import org.com.reports.dto.AccountDto;
import org.com.reports.dto.AddCustomerRequestDto;
import org.com.reports.dto.EnquiryDto;
import org.com.reports.dto.ReportResponseDto;
import org.com.reports.entity.Account;
import org.com.reports.entity.Enquiry;

public interface CibilService {

    public ReportResponseDto getReport(String pan, AddCustomerRequestDto payload);

    public Account addAccount(String pan, AccountDto payload);

    public Enquiry addEnquiry(String pan, EnquiryDto payload);
}
