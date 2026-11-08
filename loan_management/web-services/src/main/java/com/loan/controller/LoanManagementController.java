
package com.loan.controller;

import com.loan.dto.loan.DisbursementConsentDto;
import com.loan.dto.loan.LoanApplicationPayload;
import com.loan.dto.loan.LoanDecisionDto;
import com.loan.dto.loan.UpdateApplicationPayload;
import com.loan.service.LoanManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loan")
public class LoanManagementController {

    private final LoanManagementService loanManagementService;

    public LoanManagementController(LoanManagementService loanManagementService) {
        this.loanManagementService = loanManagementService;
    }

    @PostMapping("/create")
    public Object createLoanApplication(
            @RequestBody LoanApplicationPayload payload) {

        if (payload.getPanNum() == null || payload.getPanNum().isBlank()) {
            return "PAN number cannot be null or empty";
        }

        if (payload.getAadharNum() == null || payload.getAadharNum().isBlank()) {
            return "Aadhaar number cannot be null or empty";
        }

        if (payload.getMobile() == null || payload.getMobile().isBlank()) {
            return "Mobile number cannot be null or empty";
        }

        return loanManagementService.createLoanApplication(
                payload.getPanNum(),
                payload.getAadharNum(),
                payload.getMobile()
        );
    }

    @PutMapping("/update")
    public String updateLoanApplication(@RequestBody UpdateApplicationPayload payload){
        return loanManagementService.updateLoanApplication(payload);
    }

    @PostMapping("/check/{applicationNum}")
    public LoanDecisionDto processLoanApplication(@PathVariable String applicationNum){
        return loanManagementService.processLoanApplication(applicationNum);
    }

    @PostMapping("/disbursement/consent/{applicationNumber}")
    public ResponseEntity<String> processDisbursementConsent(
            @PathVariable String applicationNumber,
            @RequestBody DisbursementConsentDto payload) {

        String response = loanManagementService.processDisbursementConsent(
                applicationNumber,
                payload.getConsent());

        return ResponseEntity.ok(response);
    }
}
