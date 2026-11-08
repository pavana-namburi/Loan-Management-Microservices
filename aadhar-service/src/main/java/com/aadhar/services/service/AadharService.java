package com.aadhar.services.service;

import com.aadhar.services.dto.AadharForm;

public interface AadharService {
    String create(AadharForm af);
    AadharForm get(String num);
    boolean update(AadharForm af);
    void delete(String num);
}
