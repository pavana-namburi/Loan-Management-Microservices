package com.pan.services.controller;
import com.pan.services.dto.PanDto;
import com.pan.services.entity.Pan;
import com.pan.services.service.PanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController

public class PanController {

    @Autowired
    private PanService panService;

    @PostMapping("/create")
    public Long create(@RequestBody PanDto panDto){
        return panService.create(panDto);
    }

    @GetMapping("/get/{panNum}")
    public PanDto get(@PathVariable String panNum){
        return panService.get(panNum);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody PanDto panDto){
        return panService.update(panDto);
    }

    @DeleteMapping("delete/{panNum}")
    public Pan delete(@PathVariable String panNum){
        return panService.delete(panNum);
    }
}