package com.pan.services.dao;
import com.pan.services.entity.Pan;

public interface PanDao {

    void savePan(Pan pan);

    Pan findByPanNum(String panNum);

    boolean update(Pan pan);

    Pan delete(String num);
}
