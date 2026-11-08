package com.loan.service.impl;

import com.loan.*;
import com.loan.dto.external.cibil.AccountDto;
import com.loan.dto.external.cibil.AddCustomerRequestDto;
import com.loan.dto.external.cibil.EnquiryDto;
import com.loan.dto.external.cibil.ReportResponseDto;
import com.loan.dto.loan.LoanDecisionDto;
import com.loan.dto.loan.UpdateApplicationPayload;
import com.loan.entity.*;
import com.loan.enums.*;
import com.loan.service.CibilService;
import jakarta.transaction.Transactional;
import com.loan.service.AadharService;
import com.loan.service.PanService;
import com.loan.dto.external.aadhar.AadharDto;
import com.loan.dto.external.pan.PanDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.loan.service.LoanManagementService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoanManagementServiceImpl implements LoanManagementService {

    @Autowired
    private LoanApplicationDao loanApplicationDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private LoanDecisionDao loanDecisionDao;

    @Autowired
    private PanService panService;

    @Autowired
    private AadharService aadharService;

    @Autowired
    private CibilService cibilService;

    @Autowired
    private CibilDetailsDao cibilDetailsDao;

    @Autowired
    private LoanEligibilityServiceImpl loanEligibilityService;

    @Autowired
    private LoanDisbursementDao loanDisbursementDao;

    @Transactional
    public Object createLoanApplication(String panNum, String aadharNum, String mobile) {

        PanDto panDto = panService.getPan(panNum);
        AadharDto aadharDto = aadharService.getAadhar(aadharNum);


        if (panDto == null) {
            throw new IllegalStateException(
                    "PAN service returned no record for: " + panNum);
        }

        if (aadharDto == null) {
            throw new IllegalStateException(
                    "Aadhaar service returned no record for: " + aadharNum);
        }

        if (aadharDto.getPhone() == null
                || !aadharDto.getPhone().equals(mobile)) {
            throw new IllegalArgumentException(
                    "Mobile number does not match the Aadhaar record");
        }

        if (!panDto.getFirstname().equalsIgnoreCase(aadharDto.getFname()) ||
                !panDto.getLastname().equalsIgnoreCase(aadharDto.getLname())) {

            return "names in pan and aadhar doesnt match!!";
        }

        Customer customer = customerDao.findCustomerByPanNum(panNum);

        if (customer == null) {

            customer = new Customer();

            customer.setFullname(panDto.getFirstname() + " " + panDto.getMiddlename() + " " + panDto.getLastname());
            customer.setDateOfBirth(panDto.getDob());
            customer.setMobile(aadharDto.getPhone());
            customer.setAddress(aadharDto.getAddress());
            customer.setAadharLast4(aadharNum.substring(aadharNum.length() - 4));
            customer.setGender(aadharDto.getGender());
            customer.setPanNumber(panNum);
            customer.setCreatedAt(LocalDateTime.now());

            customerDao.saveCustomer(customer);
        }

        List<LoanApplication> applications = customer.getLoanApplications();

        LoanApplication loanApplication = (applications == null ||
                applications.isEmpty())
                ? null
                : applications.get(applications.size() - 1);

        if (loanApplication == null) {
            loanApplication = createApplication(customer);
        } else {
            switch (loanApplication.getStatus().toString()) {

                case "SUBMITTED":
                    return "Application for this user already exist!";

                case "UNDER_REVIEW":
                    return "Application for this user already exist! and it is under review";

                case "APPROVED": {

                    LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
                    LocalDate applicationDate = loanApplication.getApplicationDate();

                    if (applicationDate.isBefore(sixMonthsAgo)) {
                        loanApplication=createApplication(customer);
                    } else {
                        return "Already Application was created WITHIN the last 6 months.";
                    }
                }break;

                case "REJECTED": {

                    LocalDate AMonthAgo = LocalDate.now().minusMonths(1);
                    LocalDate applicationDate = loanApplication.getApplicationDate();

                    if (applicationDate.isBefore(AMonthAgo)) {
                        loanApplication=createApplication(customer);
                    } else {
                        return "Application was rejected WITHIN the last month.";
                    }

                }break;

                case "CANCELLED" : loanApplication=createApplication(customer);
            }
        }

        return "LoanApplication successfully created!! with num: "+loanApplication.getApplicationNumber();
    }

    @Transactional
    public LoanApplication createApplication(Customer customer) {
        LoanApplication loanApplication = new LoanApplication();

        loanApplication.setApplicationNumber(
                "LN" + UUID.randomUUID().toString().replace("-", "")
                        .substring(0, 10));
        loanApplication.setLoanType(LoanType.PERSONAL);
        loanApplication.setRequestedAmount(new BigDecimal("50000.00"));
        loanApplication.setTenureMonths(24);
        loanApplication.setApplicationDate(LocalDate.now());
        loanApplication.setStatus(ApplicationStatus.SUBMITTED);
        loanApplication.setApplicationDate(LocalDate.now());
        loanApplication.setCustomer(customer);

        loanApplicationDao.saveLoanApplication(loanApplication);

        return loanApplication;
    }

    @Transactional
    public String updateLoanApplication(UpdateApplicationPayload payload) {

        if (payload == null ||
                payload.getApplicationNumber() == null ||
                payload.getApplicationNumber().isBlank()) {
            throw new IllegalArgumentException(
                    "Application number is required");
        }

        LoanApplication loanApplication =
                loanApplicationDao.findByApplicationNumber(
                        payload.getApplicationNumber());

        if (loanApplication == null) {
            throw new IllegalArgumentException(
                    "Application not found: "
                            + payload.getApplicationNumber());
        }

        if (payload.getRequestedAmount() == null ||
                payload.getRequestedAmount().signum() <= 0 ||
                payload.getTenureMonths() == null ||
                payload.getTenureMonths() <= 0 ||
                payload.getInterestRate() == null ||
                payload.getInterestRate().signum() < 0 ||
                payload.getMonthlyIncome() == null ||
                payload.getMonthlyIncome().signum() <= 0 ||
                payload.getLoanType() == null) {
            throw new IllegalArgumentException(
                    "Invalid loan amount, tenure, rate, income or loan type");
        }

        if (loanApplication.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Only submitted applications can be processed");
        }

        loanApplication.setLoanType(payload.getLoanType());
        loanApplication.setRequestedAmount(payload.getRequestedAmount());
        loanApplication.setTenureMonths(payload.getTenureMonths());
        loanApplication.setInterestRate(payload.getInterestRate());
        loanApplication.setLoanPurpose(payload.getLoanPurpose());

        Customer customer = loanApplication.getCustomer();

        customer.setEmail(payload.getEmail());
        customer.setMonthlyIncome(payload.getMonthlyIncome());
        customer.setOccupation(payload.getOccupation());
        customer.setCity(payload.getCity());
        customer.setState(payload.getState());
        customer.setPincode(payload.getPincode());

        EnquiryDto enquiryDto=new EnquiryDto();
        enquiryDto.setEnquiryAmount(payload.getRequestedAmount().toString());
        enquiryDto.setEnquiryDate(LocalDate.now());
        enquiryDto.setEnquiryPurpose(payload.getLoanPurpose());
        enquiryDto.setMemberName("SBI Bank");

        EnquiryDto enquiryResponse = cibilService.addEnquiry( customer.getPanNumber(), enquiryDto);

        if (enquiryResponse == null) {
            throw new IllegalStateException(
                    "CIBIL enquiry could not be created. Application update failed.");
        }

        return "Application processed successfully. Application number: "
                + loanApplication.getApplicationNumber()
                + ", status: " + loanApplication.getStatus();
    }


    @Transactional
    public LoanDecisionDto processLoanApplication(String applicationNum) {

        if (applicationNum == null || applicationNum.isBlank()) {
            throw new IllegalArgumentException(
                    "Application number is required");
        }

        LoanApplication loanApplication =
                loanApplicationDao.findByApplicationNumber(applicationNum);

        if (loanApplication == null) {
            throw new IllegalArgumentException(
                    "Application not found: " + applicationNum);
        }

        if (loanApplication.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Only submitted applications can be processed. Current status: "
                            + loanApplication.getStatus());
        }

        if (!loanApplication.getLoanDecisionList().isEmpty()) {
            throw new IllegalStateException(
                    "A loan decision already exists for this application");
        }

        loanApplication.setStatus(ApplicationStatus.UNDER_REVIEW);

        Customer customer = loanApplication.getCustomer();

        String panNum = customer.getPanNumber();

        AddCustomerRequestDto payload = new AddCustomerRequestDto();
        payload.setPan(panNum);
        payload.setGender(customer.getGender());
        payload.setMobile(customer.getMobile());
        payload.setFullName(customer.getFullname());
        payload.setDateOfBirth(customer.getDateOfBirth());

        ReportResponseDto report = cibilService.getReport(
                panNum, payload);

        updateCibilDetails(report,loanApplication);

        LoanEligibilityServiceImpl.EligibilityResult result =
                loanEligibilityService.evaluate(
                        loanApplication, report);

        LoanDecision loanDecision=new LoanDecision();
        loanDecision.setApprovedAmount(result.getMaximumPrincipal().min(loanApplication.getRequestedAmount()));
        loanDecision.setApplication(loanApplication);
        loanDecision.setDecisionDate(LocalDateTime.now());
        loanDecision.setDecisionReason(result.getReason());
        loanDecision.setApprovedInterestRate(loanApplication.getInterestRate());
        loanDecision.setApprovedTenureMonths(loanApplication.getTenureMonths());

        loanApplication.getLoanDecisionList().add(loanDecision);
        loanDecision.setApplication(loanApplication);

        if(result.isEligible()) {
            loanDecision.setLoanDecisionStatus(LoanDecisionStatus.APPROVED);
            loanApplication.setStatus(ApplicationStatus.APPROVED);
        }
        else {
            loanDecision.setLoanDecisionStatus(LoanDecisionStatus.REJECTED);
            loanApplication.setStatus(ApplicationStatus.REJECTED);
        }

        LoanDecision savedDecision = loanDecisionDao.save(loanDecision);

        return populateLoanDecisionDtoFromEntity(savedDecision);
    }

    public LoanDecisionDto populateLoanDecisionDtoFromEntity(LoanDecision loanDecision){

        LoanDecisionDto loanDecisionDto=new LoanDecisionDto();

        loanDecisionDto.setDecisionId(loanDecision.getDecisionId());
        loanDecisionDto.setDecisionDate(loanDecision.getDecisionDate());
        loanDecisionDto.setDecisionReason(loanDecision.getDecisionReason());
        loanDecisionDto.setLoanDecisionStatus(loanDecision.getLoanDecisionStatus());
        loanDecisionDto.setApprovedAmount(loanDecision.getApprovedAmount());
        loanDecisionDto.setApprovedInterestRate(loanDecision.getApprovedInterestRate());
        loanDecisionDto.setApprovedTenureMonths(loanDecision.getApprovedTenureMonths());
        loanDecisionDto.setApplicationId(loanDecision.getApplication().getApplicationId());

        return loanDecisionDto;
    }

    @Transactional
    public void updateCibilDetails( ReportResponseDto report,
            LoanApplication loanApplication) {

        CibilDetails cibilDetails = new CibilDetails();

        cibilDetails.setApplication(loanApplication);
        cibilDetails.setReportReference(report.getReportId());

        if (report.getCibilScore() != null
                && report.getCibilScore().getScore() != null) {

            cibilDetails.setCreditScore(
                    report.getCibilScore().getScore().shortValue()
            );
        }

        cibilDetails.setVerificationStatus(
                VerificationStatus.VERIFIED
        );

        cibilDetails.setRemarks(
                report.getRemarks() != null
                        ? String.join(", ", report.getRemarks())
                        : null
        );

        cibilDetailsDao.save(cibilDetails);
    }


    @Transactional
    public String processDisbursementConsent( String applicationNumber,
            Boolean consent) {

        if (consent == null) {
            throw new IllegalArgumentException(
                    "Consent must be true or false");
        }

        LoanApplication application =
                loanApplicationDao.findByApplicationNumber(applicationNumber);

        if (application == null) {
            throw new IllegalArgumentException("Application not found");
        }

        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only approved applications can proceed to disbursement");
        }

        LoanDecision decision =
                loanDecisionDao.findLatestByApplicationId(
                        application.getApplicationId());

        if (decision == null ||
                decision.getLoanDecisionStatus() != LoanDecisionStatus.APPROVED) {
            throw new IllegalStateException(
                    "No approved loan decision exists");
        }

        if (!consent) {
            application.setStatus(ApplicationStatus.CANCELLED);
            return "Customer declined disbursement. Application cancelled.";
        }

        if (loanDisbursementDao.existsByApplicationId(
                application.getApplicationId())) {
            throw new IllegalStateException(
                    "A disbursement record already exists");
        }

        LoanDisbursement disbursement = new LoanDisbursement();
        disbursement.setLoanApplication(application);
        disbursement.setDisbursementAmount(decision.getApprovedAmount());
        disbursement.setLoanDisbursementStatus(
                LoanDisbursementStatus.PENDING);

        disbursement.setPaymentMode(PaymentMode.BANK_TRANSFER);

        disbursement.setTransactionReference(
                UUID.randomUUID().toString());

        Customer customer = application.getCustomer();

        AccountDto account = new AccountDto();
        account.setAccountNumber(
                "LN" + UUID.randomUUID().toString().replace("-", "")
                        .substring(0, 18));
        account.setAccountType(application.getLoanType().name());
        account.setMemberName("SBI Bank");
        account.setOwnership("INDIVIDUAL");
        account.setDateOpened(LocalDate.now());
        account.setCurrentBalance(
                decision.getApprovedAmount().toPlainString());
        account.setSanctionedAmount(
                decision.getApprovedAmount().toPlainString());
        account.setEmiAmount(
                calculateEmi(
                        decision.getApprovedAmount(),
                        decision.getApprovedInterestRate(),
                        decision.getApprovedTenureMonths())
                        .toPlainString());
        account.setTenureMonths(decision.getApprovedTenureMonths());
        account.setAccountStatus("ACTIVE");
        account.setPaymentHistory("NEW");


        try {
            AccountDto response =
                    cibilService.addAccount(customer.getPanNumber(), account);

            if (response == null) {
                throw new IllegalStateException(
                        "CIBIL service returned an empty response");
            }

            disbursement.setLoanDisbursementStatus(
                    LoanDisbursementStatus.SUCCESS);
            disbursement.setDisbursementDate(LocalDateTime.now());

            application.setStatus(ApplicationStatus.DISBURSED);

            loanDisbursementDao.save(disbursement);

            return "Disbursement completed successfully. Application: "
                    + applicationNumber;

        } catch (RuntimeException ex) {
            throw new IllegalStateException(
                    "Disbursement failed because CIBIL account creation failed: "
                            + ex.getMessage(), ex);
        }

    }

    private BigDecimal calculateEmi(BigDecimal principal,
            BigDecimal annualInterestRate,
            Integer tenureMonths) {

        if (annualInterestRate == null || annualInterestRate.signum() == 0) {
            return principal.divide(
                    BigDecimal.valueOf(tenureMonths), 2,
                    java.math.RoundingMode.HALF_UP);
        }

        double monthlyRate =
                annualInterestRate.doubleValue() / 1200.0;

        double emi = principal.doubleValue()
                * monthlyRate
                * Math.pow(1 + monthlyRate, tenureMonths)
                / (Math.pow(1 + monthlyRate, tenureMonths) - 1);

        return BigDecimal.valueOf(emi)
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }

}
