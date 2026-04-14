package com.crms.placement.model;

import java.time.LocalDateTime;

/**
 * Builder Pattern implementation for Opportunity object construction.
 * Allows flexible creation of Opportunity objects with optional fields.
 */
public class OpportunityBuilder {

    private String name;
    private String role;
    private String type;
    private String description;
    private String location;
    private Double ctc;
    private Double stipend;
    private Double minCgpa;
    private String jdUrl;
    private Integer maxBacklogs;
    private Integer vacancies;
    private LocalDateTime applicationDeadline;
    private Company company;

    // Builder methods
    public OpportunityBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public OpportunityBuilder withRole(String role) {
        this.role = role;
        return this;
    }

    public OpportunityBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public OpportunityBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public OpportunityBuilder withLocation(String location) {
        this.location = location;
        return this;
    }

    public OpportunityBuilder withCtc(Double ctc) {
        this.ctc = ctc;
        return this;
    }

    public OpportunityBuilder withStipend(Double stipend) {
        this.stipend = stipend;
        return this;
    }

    public OpportunityBuilder withMinCgpa(Double minCgpa) {
        this.minCgpa = minCgpa;
        return this;
    }

    public OpportunityBuilder withJdUrl(String jdUrl) {
        this.jdUrl = jdUrl;
        return this;
    }

    public OpportunityBuilder withMaxBacklogs(Integer maxBacklogs) {
        this.maxBacklogs = maxBacklogs;
        return this;
    }

    public OpportunityBuilder withVacancies(Integer vacancies) {
        this.vacancies = vacancies;
        return this;
    }

    public OpportunityBuilder withApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
        return this;
    }

    public OpportunityBuilder withCompany(Company company) {
        this.company = company;
        return this;
    }

    /**
     * Builds the Opportunity object with the configured fields.
     * @return Constructed Opportunity object
     * @throws IllegalArgumentException if required fields are missing
     */
    public Opportunity build() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Job name is required");
        }
        if (company == null) {
            throw new IllegalArgumentException("Company is required");
        }

        Opportunity opportunity = new Opportunity();
        opportunity.setName(name);
        opportunity.setRole(role);
        opportunity.setType(type != null ? type : "FTE");
        opportunity.setDescription(description);
        opportunity.setLocation(location);
        opportunity.setCtc(ctc);
        opportunity.setStipend(stipend);
        opportunity.setMinCgpa(minCgpa != null ? minCgpa : 0.0);
        opportunity.setJdUrl(jdUrl);
        opportunity.setMaxBacklogs(maxBacklogs != null ? maxBacklogs : 0);
        opportunity.setVacancies(vacancies != null ? vacancies : 1);
        opportunity.setApplicationDeadline(applicationDeadline);
        opportunity.setCompany(company);

        return opportunity;
    }
}
