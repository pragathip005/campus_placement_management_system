package com.crms.placement.dto;

import java.time.LocalDateTime;

public class InterviewExperienceDTO {

    private String title;
    private String description;
    private String applicantName;
    private LocalDateTime experienceDate;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public LocalDateTime getExperienceDate() { return experienceDate; }
    public void setExperienceDate(LocalDateTime experienceDate) { this.experienceDate = experienceDate; }
}