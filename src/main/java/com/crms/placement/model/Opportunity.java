package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "shortlisting_success_rate")
    private Double shortlistingSuccessRate;

    @ElementCollection
    @CollectionTable(name = "opportunity_eligible_branches", joinColumns = @JoinColumn(name = "opportunity_id"))
    @Column(name = "branch")
    private List<String> eligibleBranches;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    // Private constructor to enforce Builder Pattern
    protected Opportunity() {}

    /**
     * Static builder method for fluent object construction.
     * @return New OpportunityBuilder instance
     */
    public static OpportunityBuilder builder() {
        return new OpportunityBuilder();
    }

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
    public Double getShortlistingSuccessRate() { return shortlistingSuccessRate; }
    public List<String> getEligibleBranches() { return eligibleBranches; }

    // Setters (for builder pattern)
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setType(String type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setCtc(Double ctc) { this.ctc = ctc; }
    public void setStipend(Double stipend) { this.stipend = stipend; }
    public void setMinCgpa(Double minCgpa) { this.minCgpa = minCgpa; }
    public void setApplicationDeadline(LocalDateTime applicationDeadline) { this.applicationDeadline = applicationDeadline; }
    public void setCompany(Company company) { this.company = company; }
    public void setJdUrl(String jdUrl) { this.jdUrl = jdUrl; }
    public void setMaxBacklogs(Integer maxBacklogs) { this.maxBacklogs = maxBacklogs; }
    public void setVacancies(Integer vacancies) { this.vacancies = vacancies; }
    public void setShortlistingSuccessRate(Double shortlistingSuccessRate) { this.shortlistingSuccessRate = shortlistingSuccessRate; }
    public void setEligibleBranches(List<String> eligibleBranches) { this.eligibleBranches = eligibleBranches; }

    
}