package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "opportunities")
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer opportunity_id;

    private String name;
    private String role;
    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    private Double ctc;
    private Double stipend;
    private String jdUrl;
    private Integer maxBacklogs;
    private Integer vacancies;
    @Column(name = "min_cgpa")
    private Double minCgpa;

    @Column(name = "application_deadline")
    private LocalDateTime applicationDeadline;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    // Getters
    public Integer getOpportunity_id() { return opportunity_id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public Double getCtc() { return ctc; }
    public Double getStipend() { return stipend; }
    public Double getMinCgpa() { return minCgpa; }
    public LocalDateTime getApplicationDeadline() { return applicationDeadline; }
    public Company getCompany() { return company; }
    public String getJdUrl() { return jdUrl; }
    public Integer getMaxBacklogs() { return maxBacklogs; }
    public Integer getVacancies() { return vacancies; }
}