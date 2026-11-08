package com.pan.services.entity;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

@Entity
@Table(name="pan_details")
public class Pan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false,unique = true)
    private String pan_num;

    @Column(nullable=false)
    private String firstname;

    @Column(nullable=false)
    private String middlename;

    @Column(nullable=false)
    private String lastname;

    @Column(nullable=false)
    private String fathername;

    @Column(nullable=false)
    private LocalDate dob;

    @Column(nullable=false)
    private boolean active_indicator;

    public boolean isActive_indicator() {
        return active_indicator;
    }

    public void setActive_indicator(boolean active_indicator) {
        this.active_indicator = active_indicator;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getFathername() {
        return fathername;
    }

    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getPan_num() {
        return pan_num;
    }

    public void setPan_num(String pan_num) {
        this.pan_num = pan_num;
    }
}

