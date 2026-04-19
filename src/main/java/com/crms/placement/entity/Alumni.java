package com.crms.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alumni")
public class Alumni {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
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

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getPlacementStatus() {
        return placementStatus;
    }

    public void setPlacementStatus(String placementStatus) {
        this.placementStatus = placementStatus;
    }

    public String getHigherStudiesUniversity() {
        return higherStudiesUniversity;
    }

    public void setHigherStudiesUniversity(String higherStudiesUniversity) {
        this.higherStudiesUniversity = higherStudiesUniversity;
    }

    public String getHigherStudiesCourse() {
        return higherStudiesCourse;
    }

    public void setHigherStudiesCourse(String higherStudiesCourse) {
        this.higherStudiesCourse = higherStudiesCourse;
    }

    public String getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(String currentCompany) {
        this.currentCompany = currentCompany;
    }

    public String getCurrentJobRole() {
        return currentJobRole;
    }

    public void setCurrentJobRole(String currentJobRole) {
        this.currentJobRole = currentJobRole;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
