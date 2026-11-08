package com.pan.services.service.impl;

import com.pan.services.dao.PanDao;
import com.pan.services.dto.PanDto;
import com.pan.services.entity.Pan;
import com.pan.services.service.PanService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PanServiceImpl implements PanService {

    @Autowired
    private PanDao panDao;

    @Transactional
    public Long create(PanDto panDto){

        Pan pan=populateEntityFromPanDto(panDto);

        panDao.savePan(pan);
        return pan.getId();
    }

    public PanDto get(String panNum){
        Pan pan = panDao.findByPanNum(panNum);

        PanDto panDto=new PanDto();

        panDto.setPan_num(pan.getPan_num());
        panDto.setFirstname(pan.getFirstname());
        panDto.setMiddlename(pan.getMiddlename());
        panDto.setLastname(pan.getLastname());
        panDto.setFathername(pan.getFathername());
        panDto.setDob(pan.getDob());

        return panDto;
    }

    public Pan populateEntityFromPanDto(PanDto panDto){
        Pan pan=new Pan();

        pan.setFirstname(panDto.getFirstname());
        pan.setMiddlename(panDto.getMiddlename());
        pan.setLastname(panDto.getLastname());
        pan.setPan_num(panDto.getPan_num());
        pan.setFathername(panDto.getFathername());
        pan.setDob(panDto.getDob());
        pan.setActive_indicator(true);

        return pan;
    }

    @Transactional
    public boolean update(PanDto panDto){

        Pan pan=populateEntityFromPanDto(panDto);

        return panDao.update(pan);
    }

    @Transactional
    public Pan delete(String panNum){

        return panDao.delete(panNum);
    }
}
