
package org.com.reports.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "account", schema = "reports")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "act_id")
    private Integer actId;

    @Column(name = "act_num", length = 50)
    private String actNum;

    @Column(name = "act_type", length = 20)
    private String actType;

    @Column(name = "member_name", length = 20)
    private String memberName;

    @Column(name = "ownership", length = 30)
    private String ownership;

    @Column(name = "date_opened")
    private LocalDate dateOpened;

    @Column(name = "last_payment_date")
    private LocalDate lastPaymentDate;

    @Column(name = "current_balance", length = 30)
    private String currentBalance;

    @Column(name = "credit_limit", length = 30)
    private String creditLimit;

    @Column(name = "payment_history", length = 30)
    private String paymentHistory;

    @Column(name = "account_status", length = 10)
    private String accountStatus;

    @Column(name = "sanctioned_amount", length = 30)
    private String sanctionedAmount;

    @Column(name = "emi_amount", length = 30)
    private String emiAmount;

    @Column(name = "tenure_months", length = 30)
    private String tenureMonths;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_id")
    private Customer customer;

    public Account() {
    }

    public Integer getActId() {
        return actId;
    }

    public void setActId(Integer actId) {
        this.actId = actId;
    }

    public String getActNum() {
        return actNum;
    }

    public void setActNum(String actNum) {
        this.actNum = actNum;
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public LocalDate getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(LocalDate dateOpened) {
        this.dateOpened = dateOpened;
    }

    public LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(LocalDate lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getPaymentHistory() {
        return paymentHistory;
    }

    public void setPaymentHistory(String paymentHistory) {
        this.paymentHistory = paymentHistory;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getSanctionedAmount() {
        return sanctionedAmount;
    }

    public void setSanctionedAmount(String sanctionedAmount) {
        this.sanctionedAmount = sanctionedAmount;
    }

    public String getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(String emiAmount) {
        this.emiAmount = emiAmount;
    }

    public String getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(String tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
