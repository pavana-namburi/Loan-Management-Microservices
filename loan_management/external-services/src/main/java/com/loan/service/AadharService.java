package com.loan.service;

import com.loan.dto.external.aadhar.AadharDto;

public interface AadharService {

    AadharDto getAadhar(String aadharNum);
}
