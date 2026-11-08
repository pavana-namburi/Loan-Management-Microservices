
package org.com.reports.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "remarks", schema = "reports")
public class Remark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rem_id")
    private Integer remId;

    @Column(name = "remark", length = 100)
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_id")
    private Customer customer;

    public Remark() {
    }

    public Integer getRemId() {
        return remId;
    }

    public void setRemId(Integer remId) {
        this.remId = remId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
