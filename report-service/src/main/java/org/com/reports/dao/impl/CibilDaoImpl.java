package org.com.reports.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.com.reports.dao.CibilDao;
import org.com.reports.entity.*;
import org.springframework.stereotype.Repository;


@Repository
public class CibilDaoImpl implements CibilDao {

    @PersistenceContext
    private EntityManager em;

    public Customer findByPan(String pan){
        TypedQuery<Customer> query=em
                .createQuery(
                "SELECT c FROM Customer c WHERE c.pan=:pan",
                Customer.class
                )
                .setParameter("pan",pan);

        return query.getResultStream().findFirst().orElse(null);
    }

    public void saveCustomer(Customer customer) {
        em.persist(customer);
    }

    public void saveCibilScore(CibilScore cibilScore) {
        em.persist(cibilScore);
    }

    public void saveEnquiry(Enquiry enquiry){
        em.persist(enquiry);
    }

    public void saveAddress(Address address){
        em.persist(address);
    }

    public void saveReport(Report report){
        em.persist(report);
    }

    public void saveAccount(Account account){
        em.persist(account);
    }
}
