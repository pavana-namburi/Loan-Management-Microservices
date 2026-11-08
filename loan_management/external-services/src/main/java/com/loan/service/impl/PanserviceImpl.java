package com.loan.service.impl;

import com.loan.service.PanService;
import com.loan.dto.external.pan.PanDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
public class PanserviceImpl implements PanService {

    @Autowired
    private RestClient restClient;

    public PanDto getPan(String panNum){

        return restClient.get()
                .uri("http://localhost:8081/panservice/get/{panNum}",panNum)
                .retrieve()
                .body(PanDto.class);

    }

}