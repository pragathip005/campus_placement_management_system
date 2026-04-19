package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_slots")
public class InterviewSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Opportunity holds ctc, minCgpa, interviewDate, vacancies, and links to Company.
    // Company is accessible via opportunity.getCompany().
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @Column(name = "slot_time", nullable = false)
    private LocalDateTime slotTime;

    // Values: "SCHEDULED", "COMPLETED", "CANCELLED"
    @Column(nullable = false)
    private String status;

    public InterviewSlot() {}

    public InterviewSlot(Student student, Opportunity opportunity,
                          LocalDateTime slotTime, String status) {
        this.student = student;
        this.opportunity = opportunity;
        this.slotTime = slotTime;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Opportunity getOpportunity() { return opportunity; }
    public void setOpportunity(Opportunity opportunity) { this.opportunity = opportunity; }

    public LocalDateTime getSlotTime() { return slotTime; }
    public void setSlotTime(LocalDateTime slotTime) { this.slotTime = slotTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
