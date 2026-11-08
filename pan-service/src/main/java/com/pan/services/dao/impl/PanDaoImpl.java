package com.pan.services.dao.impl;
import com.pan.services.dao.PanDao;
import com.pan.services.entity.Pan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class PanDaoImpl implements PanDao{

    @PersistenceContext
    private EntityManager em;

    public void savePan(Pan pan){
        em.persist(pan);
    }

    public Pan findByPanNum(String panNum){
        TypedQuery<Pan> query = em.createQuery(
                "SELECT p FROM Pan p WHERE p.pan_num = :panNum",
                Pan.class
        );
        query.setParameter("panNum", panNum);

        return query.getResultStream().findFirst().orElse(null);
    }

    public boolean update(Pan pan) {
        try {
            em.merge(pan);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Pan delete(String num) {
        Pan pan=findByPanNum(num);

        if (pan != null) {
            em.remove(pan);
        }

        return pan;
    }
}
