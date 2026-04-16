package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "opportunity_id")
    private Integer opportunityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApplicationStatus status;// APPLIED, ACCEPTED, REJECTED, OFFERED

    @Column(name = "applied_date")
    private LocalDateTime appliedDate;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    private OnlineAssessment onlineAssessment;

    // Constructors
    public Application() {}

    public Application(Integer studentId, Integer opportunityId, ApplicationStatus status) {
        this.studentId = studentId;
        this.opportunityId = opportunityId;
        this.status = status;
        this.appliedDate = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Integer getOpportunityId() { return opportunityId; }
    public void setOpportunityId(Integer opportunityId) { this.opportunityId = opportunityId; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public LocalDateTime getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDateTime appliedDate) { this.appliedDate = appliedDate; }

    public OnlineAssessment getOnlineAssessment() {
       return onlineAssessment;  }

    public void setOnlineAssessment(OnlineAssessment onlineAssessment) {
       this.onlineAssessment = onlineAssessment;  }
}
