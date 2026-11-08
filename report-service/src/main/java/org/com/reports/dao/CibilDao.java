package org.com.reports.dao;

import org.com.reports.entity.*;

public interface CibilDao {

    Customer findByPan(String pan);

    void saveCustomer(Customer customer);

    void saveCibilScore(CibilScore cibilScore);

    void saveEnquiry(Enquiry enquiry);

    void saveAddress(Address address);

    void saveReport(Report report);

    void saveAccount(Account account);
}
