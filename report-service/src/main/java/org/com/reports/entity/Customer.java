package org.com.reports.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer", schema = "reports")
public class Customer {

    @Id
    @Column(name = "cust_id", nullable = false, length = 30)
    private String custId;

    @Size(max = 50)
    @Column(name = "full_name", length = 50)
    private String fullName;

    @Past
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 10)
    @Column(name = "gender", length = 10)
    private String gender;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$")
    @Size(max = 50)
    @Column(name = "pan", length = 50)
    private String pan;

    @Pattern(regexp = "^[0-9]{10,12}$")
    @Size(max = 12)
    @Column(name = "mobile", length = 12)
    private String mobile;

    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY)
    private CibilScore cibilScore;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Enquiry> enquiries = new ArrayList<>();

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Remark> remarks = new ArrayList<>();

    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY)
    private Address address;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Report> reports = new ArrayList<>();

    public Customer() {
    }

    @PrePersist
    public void generateCustId() {
        if (custId == null || custId.isBlank()) {
            UUID uuid = UUID.randomUUID();

            ByteBuffer buffer = ByteBuffer.allocate(16);
            buffer.putLong(uuid.getMostSignificantBits());
            buffer.putLong(uuid.getLeastSignificantBits());

            custId = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(buffer.array());
        }
    }
    
    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    public void setEnquiries(List<Enquiry> enquiries) {
        this.enquiries = enquiries;
    }

    public List<Remark> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<Remark> remarks) {
        this.remarks = remarks;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public CibilScore getCibilScore() {
        return cibilScore;
    }

    public void setCibilScore(CibilScore cibilScore) {
        this.cibilScore = cibilScore;
    }
}