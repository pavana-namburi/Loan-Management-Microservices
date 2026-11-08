package com.loan.service;

import com.loan.dto.external.cibil.AccountDto;
import com.loan.dto.external.cibil.AddCustomerRequestDto;
import com.loan.dto.external.cibil.EnquiryDto;
import com.loan.dto.external.cibil.ReportResponseDto;

public interface CibilService {

    ReportResponseDto getReport(String panNum, AddCustomerRequestDto payload);

    EnquiryDto addEnquiry(String panNum, EnquiryDto enquiryDto);

    AccountDto addAccount(String panNum, AccountDto accountDto);
}
