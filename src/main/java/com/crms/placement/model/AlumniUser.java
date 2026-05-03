package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alumni")
public class AlumniUser {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    private String name;

    private String email;

    private String phone;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(name = "placement_status")
    private String placementStatus;

    @Column(name = "higher_studies_university")
    private String higherStudiesUniversity;

    @Column(name = "higher_studies_course")
    private String higherStudiesCourse;

    @Column(name = "current_company")
    private String currentCompany;

    @Column(name = "current_job_role")
    private String currentJobRole;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "company_history", columnDefinition = "TEXT")
    private String companyHistory;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public AlumniUser() {}

    // Getters and Setters
    public Long getId() { return id; }
    //public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }

    public String getPlacementStatus() { return placementStatus; }
    public void setPlacementStatus(String placementStatus) { this.placementStatus = placementStatus; }

    public String getHigherStudiesUniversity() { return higherStudiesUniversity; }
    public void setHigherStudiesUniversity(String higherStudiesUniversity) { 
        this.higherStudiesUniversity = higherStudiesUniversity; 
    }

    public String getHigherStudiesCourse() { return higherStudiesCourse; }
    public void setHigherStudiesCourse(String higherStudiesCourse) { 
        this.higherStudiesCourse = higherStudiesCourse; 
    }

    public String getCurrentCompany() { return currentCompany; }
    public void setCurrentCompany(String currentCompany) { this.currentCompany = currentCompany; }

    public String getCurrentJobRole() { return currentJobRole; }
    public void setCurrentJobRole(String currentJobRole) { this.currentJobRole = currentJobRole; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getCompanyHistory() { return companyHistory; }
    public void setCompanyHistory(String companyHistory) { this.companyHistory = companyHistory; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}