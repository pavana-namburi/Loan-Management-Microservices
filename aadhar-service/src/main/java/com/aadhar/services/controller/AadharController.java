package com.aadhar.services.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aadhar.services.dto.AadharForm;
import com.aadhar.services.service.AadharService;

@RestController
public class AadharController {
	@Autowired
	private AadharService as;
	
	@PostMapping("/add")
    public String create(@RequestBody AadharForm af)
    {
    	return as.create(af);
    }
	
	@GetMapping("/get/{num}")
    public AadharForm get(@PathVariable String num)
    {
    	return as.get(num);
    }
	
	@PutMapping("/update")
    public boolean update(@RequestBody AadharForm af)
    {
    	return as.update(af);
    }
	
	@DeleteMapping("/delete/{num}")
    public void delete(@PathVariable String num)
    {
    	as.delete(num);
    }
}
