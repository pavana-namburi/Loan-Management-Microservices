
package com.loan.service.impl;

import com.loan.dto.external.cibil.AccountDto;
import com.loan.dto.external.cibil.ReportResponseDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ExistingObligationServiceImpl {

    public BigDecimal calculateExistingMonthlyEmis(
            ReportResponseDto report) {

        if (report == null || report.getAccounts() == null) {
            throw new IllegalStateException(
                    "CIBIL account information is unavailable");
        }

        BigDecimal totalEmi = BigDecimal.ZERO;

        for (AccountDto account : report.getAccounts()) {

            if (account.getAccountStatus() == null ||
                    !account.getAccountStatus()
                            .equalsIgnoreCase("Active")) {
                continue;
            }

            String reportedEmi = account.getEmiAmount();

            if (reportedEmi != null &&
                    !reportedEmi.isBlank()) {

                BigDecimal emi = new BigDecimal(reportedEmi);

                if (emi.signum() < 0) {
                    throw new IllegalStateException(
                            "Invalid EMI in CIBIL report");
                }

                totalEmi = totalEmi.add(emi);
                continue;
            }

            String accountType = account.getAccountType();

            if (accountType != null &&
                    accountType.toLowerCase()
                            .contains("credit card")) {

                String balance = account.getCurrentBalance();

                if (balance == null || balance.isBlank()) {
                    throw new IllegalStateException(
                            "Credit card balance unavailable");
                }

                BigDecimal outstanding =
                        new BigDecimal(balance);

                if (outstanding.signum() < 0) {
                    throw new IllegalStateException(
                            "Invalid credit card balance");
                }

                BigDecimal estimatedEmi = outstanding
                        .multiply(new BigDecimal("0.04"));

                totalEmi = totalEmi.add(estimatedEmi);

            } else {
                throw new IllegalStateException(
                        "Existing loan EMI unavailable for account: "
                                + accountType
                                + ". Manual review required.");
            }
        }

        return totalEmi;
    }
}
