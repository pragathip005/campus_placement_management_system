package com.crms.placement.dto;

import java.time.LocalDateTime;

import com.crms.placement.model.ApplicationStatus;

/**
 * View object passed to the Thymeleaf dashboard template.
 * Merges Application + Opportunity + Company so the template
 * has a single flat object with no null-chain risk.
 */
public class ApplicationDashboardDto {

    // From Application
    private Integer applicationId;
    private ApplicationStatus status;           // APPLIED | ACCEPTED | REJECTED | OFFERED
    private LocalDateTime appliedDate;

    // From Opportunity (via app.opportunityId → Opportunity)
    private String  opportunityName;  // opp.getName()
    private String  opportunityRole;  // opp.getRole()
    private Double  ctc;
    private Double  stipend;
    private String  type;

    // From Company (via opp.getCompany())
    private String  companyName;      // company.getName()  ← your field is "name"

    // Timeline helpers — computed in service, never stored in DB
    private int     statusIndex;      // 0=APPLIED, 1=ACCEPTED, 2=OFFERED
    private int     progressPercent;  // 0 | 33 | 66 | 100
    private boolean rejected;

    public ApplicationDashboardDto() {}

    // ── Getters & Setters ─────────────────────────────────────────

    public Integer getApplicationId()                      { return applicationId; }
    public void setApplicationId(Integer v)                { this.applicationId = v; }

    public ApplicationStatus getStatus()                   {return status;}
    public void setStatus(ApplicationStatus status)        {this.status = status;}

    public LocalDateTime getAppliedDate()                  { return appliedDate; }
    public void setAppliedDate(LocalDateTime v)            { this.appliedDate = v; }

    public String getOpportunityName()                     { return opportunityName; }
    public void setOpportunityName(String v)               { this.opportunityName = v; }

    public String getOpportunityRole()                     { return opportunityRole; }
    public void setOpportunityRole(String v)               { this.opportunityRole = v; }

    public Double getCtc()                                 { return ctc; }
    public void setCtc(Double v)                           { this.ctc = v; }

    public Double getStipend()                             { return stipend; }
    public void setStipend(Double v)                       { this.stipend = v; }

    public String getType()                                { return type; }
    public void setType(String v)                          { this.type = v; }

    public String getCompanyName()                         { return companyName; }
    public void setCompanyName(String v)                   { this.companyName = v; }

    public int getStatusIndex()                            { return statusIndex; }
    public void setStatusIndex(int v)                      { this.statusIndex = v; }

    public int getProgressPercent()                        { return progressPercent; }
    public void setProgressPercent(int v)                  { this.progressPercent = v; }

    public boolean isRejected()                            { return rejected; }
    public void setRejected(boolean v)                     { this.rejected = v; }

    /** Display salary — used in template via app.salaryDisplay */
    public String getSalaryDisplay() {
        if (ctc != null && ctc > 0)         return String.format("%.1f LPA", ctc);
        if (stipend != null && stipend > 0)  return String.format("₹%.0f/mo", stipend);
        return "N/A";
    }
}