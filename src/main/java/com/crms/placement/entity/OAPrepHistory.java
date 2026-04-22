package com.crms.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "oa_prep_history")
public class OAPrepHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumni_id", nullable = false)
    private Long alumniId;

    private String company;

    @Column(name = "oa_difficulty")
    private String oaDifficulty;

    private String questions;
    private String tips;

    @Column(name = "interview_experience")
    private String interviewExperience;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public Long getAlumniId() {
        return alumniId;
    }

    public void setAlumniId(Long alumniId) {
        this.alumniId = alumniId;
    }

    public String getCompany() {
        return company;
    }

    public String getOaDifficulty() {
        return oaDifficulty;
    }

    public String getQuestions() {
        return questions;
    }

    public String getTips() {
        return tips;
    }

    public String getInterviewExperience() {
        return interviewExperience;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}