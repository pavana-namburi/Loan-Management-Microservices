package com.loan;

import com.loan.entity.LoanDecision;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LoanDecisionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public LoanDecision save(LoanDecision loanDecision) {
        entityManager.persist(loanDecision);
        return loanDecision;
    }

    @Transactional
    public LoanDecision findLatestByApplicationId(Integer applicationId) {
        return entityManager.createQuery(
                        "SELECT d FROM LoanDecision d " +
                                "WHERE d.application.applicationId = :applicationId " +
                                "ORDER BY d.decisionDate DESC",
                        LoanDecision.class)
                .setParameter("applicationId", applicationId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}

