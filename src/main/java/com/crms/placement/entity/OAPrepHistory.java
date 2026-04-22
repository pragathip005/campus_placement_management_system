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

    @Column(columnDefinition = "TEXT")
    private String questions;

    @Column(columnDefinition = "TEXT")
    private String tips;

    @Column(name = "interview_experience", columnDefinition = "TEXT")
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

    public void setCompany(String company) {
        this.company = company;
    }

    public String getOaDifficulty() {
        return oaDifficulty;
    }

    public void setOaDifficulty(String oaDifficulty) {
        this.oaDifficulty = oaDifficulty;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getInterviewExperience() {
        return interviewExperience;
    }

    public void setInterviewExperience(String interviewExperience) {
        this.interviewExperience = interviewExperience;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}