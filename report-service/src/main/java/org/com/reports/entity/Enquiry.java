
package org.com.reports.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "enquiries", schema = "reports")
public class Enquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enq_id")
    private Integer enqId;

    @Column(name = "enquiry_date")
    private LocalDate enquiryDate;

    @Column(name = "member_name", length = 20)
    private String memberName;

    @Column(name = "enquiry_purpose", length = 20)
    private String enquiryPurpose;

    @Column(name = "enquiry_amount", length = 15)
    private String enquiryAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_id")
    private Customer customer;

    public Enquiry() {
    }

    public Integer getEnqId() {
        return enqId;
    }

    public void setEnqId(Integer enqId) {
        this.enqId = enqId;
    }

    public LocalDate getEnquiryDate() {
        return enquiryDate;
    }

    public void setEnquiryDate(LocalDate enquiryDate) {
        this.enquiryDate = enquiryDate;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getEnquiryPurpose() {
        return enquiryPurpose;
    }

    public void setEnquiryPurpose(String enquiryPurpose) {
        this.enquiryPurpose = enquiryPurpose;
    }

    public String getEnquiryAmount() {
        return enquiryAmount;
    }

    public void setEnquiryAmount(String enquiryAmount) {
        this.enquiryAmount = enquiryAmount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
