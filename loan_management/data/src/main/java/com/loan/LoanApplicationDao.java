package com.loan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.loan.entity.LoanApplication;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class LoanApplicationDao {

    @PersistenceContext
    private EntityManager em;

    public void saveLoanApplication(LoanApplication loanApplication){
        em.persist(loanApplication);
    }

    public LoanApplication findByApplicationNumber(String num) {
        TypedQuery<LoanApplication> query = em.createQuery(
                "SELECT l FROM LoanApplication l WHERE l.applicationNumber = :num",
                LoanApplication.class
        );

        query.setParameter("num", num);

        return query.getResultStream().findFirst().orElse(null);
    }
}
