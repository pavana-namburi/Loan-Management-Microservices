package org.com.reports.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cibil_score", schema = "reports")
public class CibilScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cibil_id")
    private Integer cibilId;

    @Column(name = "score", length = 20)
    private String score;

    @Column(name = "score_date")
    private LocalDate scoreDate;

    @Column(name = "risk_grade", length = 20)
    private String riskGrade;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_id")
    private Customer customer;

    public CibilScore() {
    }

    public Integer getCibilId() {
        return cibilId;
    }

    public void setCibilId(Integer cibilId) {
        this.cibilId = cibilId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public LocalDate getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(LocalDate scoreDate) {
        this.scoreDate = scoreDate;
    }

    public String getRiskGrade() {
        return riskGrade;
    }

    public void setRiskGrade(String riskGrade) {
        this.riskGrade = riskGrade;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}

