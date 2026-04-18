package com.crms.placement.model;
import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    private Long company_id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "company_id")
    private User user;

    private String name;
    private String email;
    private String industry;

    public Company() {}

    // getters & setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getCompany_id() { return company_id; }
    public void setCompany_id(Long company_id) { this.company_id = company_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
}