package com.loan.service.impl;

import com.loan.dto.external.cibil.AccountDto;
import com.loan.dto.external.cibil.EnquiryDto;
import com.loan.service.CibilService;
import com.loan.dto.external.cibil.AddCustomerRequestDto;
import com.loan.dto.external.cibil.ReportResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CibilServiceImpl implements CibilService {

    @Autowired
    private RestClient restClient;

    public ReportResponseDto getReport(String panNum, AddCustomerRequestDto payload){

        return restClient.post()
                .uri("http://localhost:8083/cibilservice/generate/{panNum}",panNum)
                .body(payload)
                .retrieve()
                .body(ReportResponseDto.class);

    }

    public EnquiryDto addEnquiry(String panNum, EnquiryDto enquiryDto){

        return restClient.post()
                .uri("http://localhost:8083/cibilservice/enquiries/{panNum}",panNum)
                .body(enquiryDto)
                .retrieve()
                .body(EnquiryDto.class);
    }

    public AccountDto addAccount(String panNum, AccountDto accountDto) {
        return restClient.post()
                .uri("http://localhost:8083/cibilservice/accounts/{panNum}", panNum)
                .body(accountDto)
                .retrieve()
                .body(AccountDto.class);
    }
}
