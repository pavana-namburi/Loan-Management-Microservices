package com.loan;

import com.loan.entity.CibilDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CibilDetailsDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public CibilDetails save(CibilDetails cibilDetails) {
        entityManager.persist(cibilDetails);
        return cibilDetails;
    }
}

