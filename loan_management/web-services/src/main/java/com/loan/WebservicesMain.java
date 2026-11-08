package com.loan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class WebservicesMain {
    public static void main(String[] args) {
        System.out.println("Loan Management Started");
        SpringApplication.run(WebservicesMain.class, args);
    }
}