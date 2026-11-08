package com.pan.services.service;

import com.pan.services.dto.PanDto;
import com.pan.services.entity.Pan;

public interface PanService {

    Long create(PanDto panDto);

    PanDto get(String panNum);

    boolean update(PanDto panDto);

    Pan delete(String panNum);
}
