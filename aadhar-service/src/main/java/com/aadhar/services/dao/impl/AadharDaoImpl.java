package com.aadhar.services.dao.impl;

import org.springframework.stereotype.Repository;

import com.aadhar.services.dao.AadharDao;
import com.aadhar.services.entity.Aadhar;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class AadharDaoImpl implements AadharDao {
	@PersistenceContext
	private EntityManager em;
    public String create(Aadhar ad) {
        Aadhar aadhar=get(ad.getAadharNum());
        if(aadhar==null)
        {
            System.out.println("ID before persist: " + ad.toString());
        	em.persist(ad);
        	return "Aadhar record created successfully with Id:"+ad.getId();
        }
        else
        	return "Aadhar record already existing with the given aadhar number";
    }

    public Aadhar get(String num) {
    	TypedQuery<Aadhar> query = em.createQuery(
                "SELECT p FROM Aadhar p WHERE p.aadharNum = :aadharNum", Aadhar.class);
            query.setParameter("aadharNum", num);
            
            // Safely returns the single entity if found, or null if 0 results exist
            return query.getResultStream().findFirst().orElse(null);
    }

    public boolean update(Aadhar ad) {
    	try {
            em.merge(ad);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void delete(String num) {
    	Aadhar aadhar = get(num);
        if (aadhar != null) {
            em.remove(aadhar);
        }
    }
}
