package com.loan;

import com.loan.entity.LoanDisbursement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class LoanDisbursementDao {

    @PersistenceContext
    private EntityManager entityManager;

    public LoanDisbursement save(LoanDisbursement disbursement) {
        entityManager.persist(disbursement);
        entityManager.flush();
        return disbursement;
    }

    public boolean existsByApplicationId(Integer applicationId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(d) FROM LoanDisbursement d " +
                                "WHERE d.loanApplication.applicationId = :applicationId",
                        Long.class)
                .setParameter("applicationId", applicationId)
                .getSingleResult();

        return count > 0;
    }
}

