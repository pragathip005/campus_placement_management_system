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
    private Integer duration;

    @Column(name = "min_cgpa")
    private Double minCgpa;

    @Column(name = "application_deadline")
    private LocalDateTime applicationDeadline;

    @Column(name = "shortlisting_success_rate")
    private Double shortlistingSuccessRate;

    @Column(name = "oa_date")
    private LocalDateTime oaDate;

    @Column(name = "interview_date")
    private LocalDateTime interviewDate;

    // ✅ Keep your hasOa (used in ApplicationManager)
    @Column(name = "has_oa")
    private Boolean hasOa;

    // ✅ Keep HR's hasInterview
    @Column(name = "has_interview")
    private Boolean hasInterview;

    // ✅ Keep your @ElementCollection (works with existing DB schema)
    @ElementCollection
    @CollectionTable(name = "opportunity_eligible_branches", joinColumns = @JoinColumn(name = "opportunity_id"))
    @Column(name = "branch")
    private List<String> eligibleBranches;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    protected Opportunity() {}

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
    public Integer getDuration() { return duration; }
    public Double getShortlistingSuccessRate() { return shortlistingSuccessRate; }
    public List<String> getEligibleBranches() { return eligibleBranches; }
    public LocalDateTime getOaDate() { return oaDate; }
    public LocalDateTime getInterviewDate() { return interviewDate; }
    public Boolean getHasOa() { return hasOa; }
    public Boolean getHasInterview() { return hasInterview; }

    // Setters
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
    public void setDuration(Integer duration) { this.duration = duration; }
    public void setShortlistingSuccessRate(Double shortlistingSuccessRate) { this.shortlistingSuccessRate = shortlistingSuccessRate; }
    public void setEligibleBranches(List<String> eligibleBranches) { this.eligibleBranches = eligibleBranches; }
    public void setOaDate(LocalDateTime oaDate) { this.oaDate = oaDate; }
    public void setInterviewDate(LocalDateTime interviewDate) { this.interviewDate = interviewDate; }
    public void setHasOa(Boolean hasOa) { this.hasOa = hasOa; }
    public void setHasInterview(Boolean hasInterview) { this.hasInterview = hasInterview; }
}