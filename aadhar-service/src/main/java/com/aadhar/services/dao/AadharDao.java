package com.aadhar.services.dao;

import com.aadhar.services.entity.Aadhar;

public interface AadharDao {
    String create(Aadhar ad);
    Aadhar get(String num);
    boolean update(Aadhar ad);
    void delete(String num);

}
