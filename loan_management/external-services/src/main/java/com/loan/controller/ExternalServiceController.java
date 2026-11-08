package com.loan.controller;


import com.loan.service.AadharService;
import com.loan.service.CibilService;
import com.loan.service.PanService;
import com.loan.dto.external.aadhar.AadharDto;
import com.loan.dto.external.cibil.AddCustomerRequestDto;
import com.loan.dto.external.cibil.ReportResponseDto;
import com.loan.dto.external.pan.PanDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/external")
public class ExternalServiceController {

    @Autowired
    private PanService panService;

    @Autowired
    private AadharService aadharService;

    @Autowired
    private CibilService cibilService;

    @GetMapping("/aadhar/{aadharNum}")
    public AadharDto getAadhar(@PathVariable("aadharNum") String aadharNum) {
        return aadharService.getAadhar(aadharNum);
    }

    @GetMapping("/pan/{panNum}")
    public PanDto getPan(@PathVariable("panNum") String panNum) {
        return panService.getPan(panNum);
    }

    @PostMapping("/report/{panNum}")
    public ReportResponseDto getReport(
            @PathVariable("panNum") String panNum,
            @RequestBody AddCustomerRequestDto payload) {
        return cibilService.getReport(panNum, payload);
    }
}
