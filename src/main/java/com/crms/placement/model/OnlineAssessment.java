package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "online_assessments")
public class OnlineAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;   // 🔥 match Application (Integer, not Long)

    @Column(name = "oa_link")
    private String oaLink;

    private Integer score;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    private boolean completed;

    // 🔗 Relationship with Application
    @OneToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    // Constructors
    public OnlineAssessment() {}

    public OnlineAssessment(Application application, String oaLink) {
        this.application = application;
        this.oaLink = oaLink;
        this.completed = false;
        this.scheduledAt = LocalDateTime.now(); // 🔥 set automatically
    }

    // Getters & Setters
    public Integer getId() { return id; }

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