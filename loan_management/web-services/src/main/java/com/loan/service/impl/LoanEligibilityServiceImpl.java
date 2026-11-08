
package com.loan.service.impl;

import com.loan.dto.external.cibil.ReportResponseDto;
import com.loan.entity.Customer;
import com.loan.entity.LoanApplication;
import com.loan.entity.LoanDecision;
import com.loan.enums.LoanDecisionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class LoanEligibilityServiceImpl {

    @Autowired
    private EmiCalculatorServiceImpl emiCalculator;

    @Autowired
    private ExistingObligationServiceImpl obligationService;

    public EligibilityResult evaluate(
            LoanApplication application,
            ReportResponseDto report) {

        if (report == null || report.getCibilScore() == null ||
                report.getCibilScore().getScore() == null) {
            throw new IllegalStateException(
                    "CIBIL score is unavailable");
        }

        int score = report.getCibilScore().getScore();

        if (score <= 750) {
            return new EligibilityResult(
                    false, "CIBIL score must be greater than 750",
                    BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO);
        }

        Customer customer = application.getCustomer();

        BigDecimal income = customer.getMonthlyIncome();

        if (income == null || income.signum() <= 0) {
            throw new IllegalStateException(
                    "Valid monthly income is required");
        }

        BigDecimal existingEmis =
                obligationService.calculateExistingMonthlyEmis(report);

        // Maximum total monthly debt obligation = 75% of income.
        BigDecimal maximumTotalEmi = income
                .multiply(new BigDecimal("0.75"));

        // Subtract existing monthly debt obligations.
        BigDecimal affordableEmi = maximumTotalEmi
                .subtract(existingEmis);

        if (affordableEmi.signum() <= 0) {
            return new EligibilityResult(
                    false, "Existing obligations exceed the EMI limit",
                    existingEmis, BigDecimal.ZERO,
                    BigDecimal.ZERO);
        }

        if (application.getRequestedAmount() == null ||
                application.getInterestRate() == null ||
                application.getTenureMonths() == null) {
            throw new IllegalStateException(
                    "Loan amount, interest rate and tenure are required");
        }

        BigDecimal requestedEmi = emiCalculator.calculateEmi(
                application.getRequestedAmount(),
                application.getInterestRate(),
                application.getTenureMonths());

        BigDecimal maximumPrincipal =
                emiCalculator.calculateMaximumPrincipal(
                        affordableEmi,
                        application.getInterestRate(),
                        application.getTenureMonths());

        boolean eligible = requestedEmi.compareTo(affordableEmi) <= 0
                && maximumPrincipal.compareTo(new BigDecimal("100000")) >= 0;

        String reason = eligible
                ? "CIBIL and EMI affordability checks passed"
                : "Requested loan EMI exceeds affordable monthly EMI";

        return new EligibilityResult(
                eligible, reason, existingEmis,
                affordableEmi, maximumPrincipal);
    }

    public static class EligibilityResult {

        private final boolean eligible;
        private final String reason;
        private final BigDecimal existingEmis;
        private final BigDecimal affordableEmi;
        private final BigDecimal maximumPrincipal;

        public EligibilityResult(
                boolean eligible,
                String reason,
                BigDecimal existingEmis,
                BigDecimal affordableEmi,
                BigDecimal maximumPrincipal) {
            this.eligible = eligible;
            this.reason = reason;
            this.existingEmis = existingEmis;
            this.affordableEmi = affordableEmi;
            this.maximumPrincipal = maximumPrincipal;
        }

        public boolean isEligible() {
            return eligible;
        }

        public String getReason() {
            return reason;
        }

        public BigDecimal getExistingEmis() {
            return existingEmis;
        }

        public BigDecimal getAffordableEmi() {
            return affordableEmi;
        }

        public BigDecimal getMaximumPrincipal() {
            return maximumPrincipal;
        }
    }
}
