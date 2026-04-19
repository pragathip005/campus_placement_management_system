package com.crms.placement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    private Integer company_id;

    private String name;

    private String industry;

    @Column(name = "email")
    private String email;

    public Company() {}

    public Company(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Integer getCompany_id() { return company_id; }
    public void setCompany_id(Integer company_id) { this.company_id = company_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}