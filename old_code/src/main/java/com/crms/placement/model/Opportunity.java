package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ORIGINAL CODE - Before Builder Pattern Implementation
 * =====================================================
 *
 * VIOLATIONS:
 * ❌ GRASP Creator Pattern
 *    - Public default constructor creates uninitialized objects
 *    - Would need multiple constructors for different field combinations
 *    - "Telescoping Constructor Problem"
 *
 * PROBLEMS:
 * - 11 fields, but default constructor doesn't initialize any
 * - Can create incomplete/invalid Opportunity objects
 * - No validation at construction time
 * - Hard to know which fields are required vs optional
 * - Would need many overloaded constructors if expanded
 *   Example would become:
 *   - Opportunity(String name, Company company)
 *   - Opportunity(String name, Company company, String role)
 *   - Opportunity(String name, Company company, String role, Double ctc)
 *   - ... and so on (constructor hell)
 */
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

    /**
     * ❌ PROBLEMATIC - Default constructor creates uninitialized object
     *
     * Issues:
     * - No validation
     * - Can't tell which fields are required
     * - All fields are null initially
     */
    public Opportunity() {}

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

    // Setters (for builder pattern) - Note: Builder pattern wasn't used before
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
}
