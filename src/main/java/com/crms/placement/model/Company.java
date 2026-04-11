package com.crms.placement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer company_id;

    private String name;

    public Integer getCompany_id() { return company_id; }
    public String getName() { return name; }
}