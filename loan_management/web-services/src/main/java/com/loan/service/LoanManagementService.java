package com.loan.service;

import com.loan.dto.loan.LoanDecisionDto;
import com.loan.dto.loan.UpdateApplicationPayload;

public interface LoanManagementService {

    Object createLoanApplication(String panNum, String aadharNum, String mobile);

    String updateLoanApplication(UpdateApplicationPayload payload);

    LoanDecisionDto processLoanApplication(String applicationNum);

    String processDisbursementConsent( String applicationNumber,
                                       Boolean consent);
}
