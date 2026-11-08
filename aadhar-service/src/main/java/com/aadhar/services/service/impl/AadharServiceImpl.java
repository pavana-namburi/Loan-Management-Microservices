package com.aadhar.services.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aadhar.services.dao.AadharDao;
import com.aadhar.services.dto.AadharForm;
import com.aadhar.services.entity.Aadhar;
import com.aadhar.services.service.AadharService;

@Service
public class AadharServiceImpl implements AadharService {
	@Autowired
	private AadharDao ad;
    public String create(AadharForm af) {
    	Aadhar a =new Aadhar();

    	a.setAadharNum(af.getAadharNum());
    	a.setFname(af.getFname());
    	a.setLname(af.getLname());
		a.setFathername(af.getFathername());
    	a.setDob(af.getDob());
    	a.setGender(af.getGender());
    	a.setAddress(af.getAddress());
    	a.setEmail(af.getEmail());
    	a.setDob(af.getDob());
    	a.setMname(af.getMname());
    	a.setPhone(af.getPhone());

        return ad.create(a);
    }

    public AadharForm get(String num) {
        AadharForm a = new AadharForm();

        Aadhar af=ad.get(num);
        if(af==null)
        	return null;

        a.setId(af.getId());
    	a.setAadharNum(af.getAadharNum());
    	a.setFname(af.getFname());
    	a.setLname(af.getLname());
    	a.setDob(af.getDob());
		a.setFathername(af.getFathername());
    	a.setGender(af.getGender());
    	a.setAddress(af.getAddress());
    	a.setEmail(af.getEmail());
    	a.setDob(af.getDob());
    	a.setMname(af.getMname());
    	a.setPhone(af.getPhone());

    	return a;
    }

    public boolean update(AadharForm af) {
        Aadhar a=new Aadhar();

        a.setId(af.getId());
    	a.setAadharNum(af.getAadharNum());
    	a.setFname(af.getFname());
    	a.setLname(af.getLname());
    	a.setDob(af.getDob());
		a.setFathername(af.getFathername());
    	a.setGender(af.getGender());
    	a.setAddress(af.getAddress());
    	a.setEmail(af.getEmail());
    	a.setDob(af.getDob());
    	a.setMname(af.getMname());
    	a.setPhone(af.getPhone());

    	return ad.update(a);
    }

    public void delete(String num) {
    	ad.delete(num);
    }
}
