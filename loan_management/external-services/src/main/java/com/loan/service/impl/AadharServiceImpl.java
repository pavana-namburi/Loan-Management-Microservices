package com.loan.service.impl;

import com.loan.service.AadharService;
import com.loan.dto.external.aadhar.AadharDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AadharServiceImpl implements AadharService {

    @Autowired
    private RestClient restClient;

    public AadharDto getAadhar(String aadharNum){

        return restClient.get()
                .uri("http://localhost:8082/aadharservice/get/{aadharNum}",aadharNum)
                .retrieve()
                .body(AadharDto.class);
    }
}
