package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "alumni")
public class Alumni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String phone;

    private Integer graduationYear;

    private String placementStatus;

    @Column(columnDefinition = "TEXT")
    private String higherStudiesUniversity;

    @Column(columnDefinition = "TEXT")
    private String higherStudiesCourse;

    @Column(columnDefinition = "TEXT")
    private String currentCompany;

    @Column(columnDefinition = "TEXT")
    private String currentJobRole;

    @Column(columnDefinition = "TEXT")
    private String linkedinUrl;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    // @ElementCollection
    // @CollectionTable(name = "alumni_company_history", joinColumns = @JoinColumn(name = "alumni_id"))
    // @Column(name = "company")
    // private List<String> companyHistory;

    public Alumni() {}

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
    public void setHigherStudiesUniversity(String higherStudiesUniversity) { this.higherStudiesUniversity = higherStudiesUniversity; }

    public String getHigherStudiesCourse() { return higherStudiesCourse; }
    public void setHigherStudiesCourse(String higherStudiesCourse) { this.higherStudiesCourse = higherStudiesCourse; }

    public String getCurrentCompany() { return currentCompany; }
    public void setCurrentCompany(String currentCompany) { this.currentCompany = currentCompany; }

    public String getCurrentJobRole() { return currentJobRole; }
    public void setCurrentJobRole(String currentJobRole) { this.currentJobRole = currentJobRole; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    // public List<String> getCompanyHistory() { return companyHistory; }
    // public void setCompanyHistory(List<String> companyHistory) { this.companyHistory = companyHistory; }
}