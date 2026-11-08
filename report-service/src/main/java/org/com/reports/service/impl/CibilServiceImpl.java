package org.com.reports.service.impl;

import org.com.reports.dao.CibilDao;
import org.com.reports.dao.impl.CibilDaoImpl;
import org.com.reports.dto.*;
import org.com.reports.entity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class CibilServiceImpl {

    private final CibilDao cibilDao;

    public CibilServiceImpl(CibilDaoImpl customerDao) {
        this.cibilDao = customerDao;
    }

    @Transactional
    public ReportResponseDto generateReport(String pan,AddCustomerRequestDto payload) {

        Customer customer = cibilDao.findByPan(pan);

        if (customer == null) {
            customer = addCustomer(payload);
        }

        ReportResponseDto reportDto = new ReportResponseDto();

        reportDto.setGeneratedOn(Instant.now());

        CustomerDto customerDto = new CustomerDto();
        customerDto.setFullName(customer.getFullName());
        customerDto.setDateOfBirth(customer.getDateOfBirth());
        customerDto.setGender(customer.getGender());
        customerDto.setPan(customer.getPan());
        customerDto.setMobile(customer.getMobile());

        Address address = customer.getAddress();

        if (address != null) {
            AddressDto addressDto = new AddressDto();
            addressDto.setLine1(address.getLine1());
            addressDto.setLine2(address.getLine2());
            addressDto.setCity(address.getCity());
            addressDto.setState(address.getState());
            addressDto.setPincode(address.getPincode());

            customerDto.setAddress(addressDto);
        }

        reportDto.setCustomer(customerDto);

        CibilScore cibilScore = customer.getCibilScore();

        if (cibilScore != null) {
            CibilScoreDto scoreDto = new CibilScoreDto();

            scoreDto.setScore(
                    cibilScore.getScore() == null
                            ? null
                            : Integer.valueOf(cibilScore.getScore())
            );
            scoreDto.setScoreDate(cibilScore.getScoreDate());
            scoreDto.setRiskGrade(cibilScore.getRiskGrade());

            reportDto.setCibilScore(scoreDto);
        }

        List<AccountDto> accountDtos = customer.getAccounts()
                .stream()
                .map(account -> {
                    AccountDto dto = new AccountDto();

                    dto.setAccountNumber(account.getActNum());
                    dto.setAccountType(account.getActType());
                    dto.setMemberName(account.getMemberName());
                    dto.setOwnership(account.getOwnership());
                    dto.setDateOpened(account.getDateOpened());
                    dto.setLastPaymentDate(account.getLastPaymentDate());
                    dto.setCurrentBalance(account.getCurrentBalance());
                    dto.setCreditLimit(account.getCreditLimit());
                    dto.setPaymentHistory(account.getPaymentHistory());
                    dto.setAccountStatus(account.getAccountStatus());
                    dto.setSanctionedAmount(account.getSanctionedAmount());
                    dto.setEmiAmount(account.getEmiAmount());

                    dto.setTenureMonths(
                            account.getTenureMonths() == null
                                    ? null
                                    : Integer.valueOf(account.getTenureMonths())
                    );

                    return dto;
                })
                .toList();

        reportDto.setAccounts(accountDtos);

        List<EnquiryDto> enquiryDtos = customer.getEnquiries()
                .stream()
                .map(enquiry -> {
                    EnquiryDto dto = new EnquiryDto();

                    dto.setEnquiryDate(enquiry.getEnquiryDate());
                    dto.setMemberName(enquiry.getMemberName());
                    dto.setEnquiryPurpose(enquiry.getEnquiryPurpose());
                    dto.setEnquiryAmount(enquiry.getEnquiryAmount());

                    return dto;
                })
                .toList();

        reportDto.setEnquiries(enquiryDtos);

        List<String> remarks = customer.getRemarks()
                .stream()
                .map(Remark::getRemark)
                .toList();

        reportDto.setRemarks(remarks);

        Report report = new Report();
        report.setCustomer(customer);
        report.setGeneratedOn(LocalDate.now());

        cibilDao.saveReport(report);

        reportDto.setReportId(String.valueOf(report.getRepId()));

        return reportDto;
    }

    @Transactional
    public Integer addAccount(String pan, AccountDto payload) {

        Customer customer = cibilDao.findByPan(pan);

        Account account = new Account();

        account.setActNum(payload.getAccountNumber());
        account.setActType(payload.getAccountType());
        account.setMemberName(payload.getMemberName());
        account.setOwnership(payload.getOwnership());
        account.setDateOpened(payload.getDateOpened());
        account.setLastPaymentDate(payload.getLastPaymentDate());
        account.setCurrentBalance(payload.getCurrentBalance());
        account.setCreditLimit(payload.getCreditLimit());
        account.setPaymentHistory(payload.getPaymentHistory());
        account.setAccountStatus(payload.getAccountStatus());
        account.setSanctionedAmount(payload.getSanctionedAmount());
        account.setEmiAmount(payload.getEmiAmount());

        account.setTenureMonths(
                payload.getTenureMonths() == null
                        ? null
                        : payload.getTenureMonths().toString()
        );

        account.setCustomer(customer);
        customer.getAccounts().add(account);

        cibilDao.saveAccount(account);

        return account.getActId();
    }

    @Transactional
    public Integer addEnquiry(String pan, EnquiryDto payload) {

        Customer customer = cibilDao.findByPan(pan);

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer not found for PAN: " + pan
            );
        }

        Enquiry enquiry = new Enquiry();

        enquiry.setEnquiryDate(payload.getEnquiryDate());
        enquiry.setMemberName(payload.getMemberName());
        enquiry.setEnquiryPurpose(payload.getEnquiryPurpose());
        enquiry.setEnquiryAmount(payload.getEnquiryAmount());

        enquiry.setCustomer(customer);
        customer.getEnquiries().add(enquiry);

        cibilDao.saveEnquiry(enquiry);

        return enquiry.getEnqId();
    }

    public Customer addCustomer(AddCustomerRequestDto payload) {

        Customer customer = new Customer();
        customer.setPan(payload.getPan());
        customer.setFullName(payload.getFullName());
        customer.setDateOfBirth(payload.getDateOfBirth());
        customer.setGender(payload.getGender());
        customer.setMobile(payload.getMobile());

        cibilDao.saveCustomer(customer);

        AddressDto addressPayload = payload.getAddress();

        if (addressPayload != null) {
            Address address = new Address();

            address.setLine1(addressPayload.getLine1());
            address.setLine2(addressPayload.getLine2());
            address.setCity(addressPayload.getCity());
            address.setState(addressPayload.getState());
            address.setPincode(addressPayload.getPincode());

            address.setCustomer(customer);
            customer.setAddress(address);

            cibilDao.saveAddress(address);
        }

        CibilScore cibilScore = new CibilScore();

        cibilScore.setScore("780");
        cibilScore.setScoreDate(LocalDate.now());

        cibilScore.setCustomer(customer);
        customer.setCibilScore(cibilScore);

        cibilDao.saveCibilScore(cibilScore);

        return customer;
    }
}
