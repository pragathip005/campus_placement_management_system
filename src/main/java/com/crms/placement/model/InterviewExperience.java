package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_experiences")
public class InterviewExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String applicantName;

    @Column(name = "experience_date")
    private LocalDateTime experienceDate;

    // Constructors
    public InterviewExperience() {}

    public InterviewExperience(Company company, String title, String description, String applicantName, LocalDateTime experienceDate) {
        this.company = company;
        this.title = title;
        this.description = description;
        this.applicantName = applicantName;
        this.experienceDate = experienceDate;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public LocalDateTime getExperienceDate() { return experienceDate; }
    public void setExperienceDate(LocalDateTime experienceDate) { this.experienceDate = experienceDate; }
}
