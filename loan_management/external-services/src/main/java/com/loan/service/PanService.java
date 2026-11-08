package com.loan.service;

import com.loan.dto.external.pan.PanDto;

public interface PanService {

    PanDto getPan(String panNum);
}
