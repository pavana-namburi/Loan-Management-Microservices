
package org.com.reports.controller;

import org.com.reports.dto.AccountDto;
import org.com.reports.dto.AddCustomerRequestDto;
import org.com.reports.dto.EnquiryDto;
import org.com.reports.dto.ReportResponseDto;
import org.com.reports.service.impl.CibilServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CibilController {

    private final CibilServiceImpl cibilService;

    public CibilController(CibilServiceImpl reportService) {
        this.cibilService = reportService;
    }

    @PostMapping("/generate/{pan}")
    public ResponseEntity<ReportResponseDto> generateCibilReport(
            @PathVariable String pan, @RequestBody AddCustomerRequestDto payload) {

        if(pan==null)
            return null;

        ReportResponseDto report = cibilService.generateReport(pan,payload);

        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(report);
    }

    @PostMapping("/accounts/{pan}")
    public ResponseEntity<AccountDto> addAccount(
            @PathVariable String pan,
            @RequestBody AccountDto payload) {

        Integer id = cibilService.addAccount(pan, payload);

        if(id!=null)
            return ResponseEntity.status(HttpStatus.CREATED).body(payload);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
    }

    @PostMapping("/enquiries/{pan}")
    public ResponseEntity<EnquiryDto> addEnquiry(
            @PathVariable String pan,
            @RequestBody EnquiryDto payload) {

        Integer id = cibilService.addEnquiry(pan, payload);

        if(id!=null)
            return ResponseEntity.status(HttpStatus.CREATED).body(payload);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
    }
}
