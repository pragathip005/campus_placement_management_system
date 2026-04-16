package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class OnlineAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String oaLink;

    private Integer score;

    private LocalDateTime scheduledAt;

    private boolean completed;

    // 🔗 Relationship with Application
    @OneToOne
    @JoinColumn(name = "application_id")
    private Application application;

    // Constructors
    public OnlineAssessment() {}

    public OnlineAssessment(String oaLink, Application application) {
        this.oaLink = oaLink;
        this.application = application;
        this.completed = false;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public String getOaLink() { return oaLink; }
    public void setOaLink(String oaLink) { this.oaLink = oaLink; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }
}