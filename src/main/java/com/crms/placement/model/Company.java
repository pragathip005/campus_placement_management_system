package com.crms.placement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer company_id;

    private String name;

    public Company() {}

    public Company(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Integer getCompany_id() { return company_id; }
    public void setCompany_id(Integer company_id) { this.company_id = company_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}