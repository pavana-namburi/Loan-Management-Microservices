package com.loan.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

@Service
public class EmiCalculatorServiceImpl {

    public BigDecimal calculateEmi(
            BigDecimal principal,
            BigDecimal annualInterestRate,
            int tenureMonths) {

        if (principal == null || principal.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Principal must be positive");
        }

        if (annualInterestRate == null ||
                annualInterestRate.signum() < 0) {
            throw new IllegalArgumentException(
                    "Interest rate cannot be negative or null");
        }

        if (tenureMonths <= 0) {
            throw new IllegalArgumentException(
                    "Tenure must be positive");
        }

        BigDecimal monthlyRate = annualInterestRate
                .divide(BigDecimal.valueOf(1200),
                        12, RoundingMode.HALF_UP);

        if (monthlyRate.signum() == 0) {
            return principal.divide(
                    BigDecimal.valueOf(tenureMonths),
                    2, RoundingMode.HALF_UP);
        }

        double r = monthlyRate.doubleValue();
        double factor = Math.pow(1 + r, tenureMonths);

        double emi = principal.doubleValue()
                * r * factor / (factor - 1);

        return BigDecimal.valueOf(emi)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateMaximumPrincipal(
            BigDecimal affordableEmi,
            BigDecimal annualInterestRate,
            int tenureMonths) {

        if (affordableEmi == null ||
                affordableEmi.signum() <= 0) {
            return BigDecimal.ZERO;
        }

        if (annualInterestRate == null ||
                annualInterestRate.signum() < 0 ||
                tenureMonths <= 0) {
            throw new IllegalArgumentException(
                    "Invalid rate or tenure");
        }

        BigDecimal monthlyRate = annualInterestRate
                .divide(BigDecimal.valueOf(1200),
                        12, RoundingMode.HALF_UP);

        if (monthlyRate.signum() == 0) {
            return affordableEmi
                    .multiply(BigDecimal.valueOf(tenureMonths))
                    .setScale(2, RoundingMode.DOWN);
        }

        double r = monthlyRate.doubleValue();
        double factor = Math.pow(1 + r, tenureMonths);

        double principal = affordableEmi.doubleValue()
                * (factor - 1) / (r * factor);

        return BigDecimal.valueOf(principal)
                .setScale(2, RoundingMode.DOWN);
    }
}
