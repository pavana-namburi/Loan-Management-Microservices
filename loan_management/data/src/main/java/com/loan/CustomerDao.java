package com.loan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.loan.entity.Customer;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager em;

    public void saveCustomer(Customer customer){
        em.persist(customer);
    }

    public Customer findCustomerByPanNum(String panNum) {
        TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.panNumber = :panNum",
                Customer.class
        );

        query.setParameter("panNum", panNum);

        return query.getResultStream().findFirst().orElse(null);
    }
}
