package com.crms.placement.dto;

import java.util.List;
import java.util.Map;

// DTO Pattern: Single response object that wraps all admin dashboard statistics.
// Decouples API shape from internal repository/entity structure.
public class AdminStatsDto {

    private long totalStudents;
    private long totalPlaced;
    private double placementPercentage;
    private double averageCtc;          // raw DB value
    private double highestCtc;          // raw DB value
    private long totalCompanies;
    private double offerAcceptanceRate;

    // Branch name → count of placed students
    private Map<String, Long> placementsByBranch;

    // Ranked list of companies by offer count
    private List<CompanyOfferDto> topHiringCompanies;

    // Month abbreviation (e.g. "Jan", "Feb") → offer count
    private Map<String, Long> monthlyPlacementTrend;

    // Tier label → offer count
    private Map<String, Long> offersByCtcTier;

    // ApplicationStatus name → count (for pipeline funnel chart)
    private Map<String, Long> applicationPipeline;

    // Detail lists — used by clickable modal cards
    private List<PlacedStudentDto> placedStudentsList;
    private List<Map<String, String>> companiesList; // keys: name, industry

    public AdminStatsDto() {}

    // --- Getters & Setters ---

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalPlaced() { return totalPlaced; }
    public void setTotalPlaced(long totalPlaced) { this.totalPlaced = totalPlaced; }

    public double getPlacementPercentage() { return placementPercentage; }
    public void setPlacementPercentage(double placementPercentage) { this.placementPercentage = placementPercentage; }

    public double getAverageCtc() { return averageCtc; }
    public void setAverageCtc(double averageCtc) { this.averageCtc = averageCtc; }

    public double getHighestCtc() { return highestCtc; }
    public void setHighestCtc(double highestCtc) { this.highestCtc = highestCtc; }

    public long getTotalCompanies() { return totalCompanies; }
    public void setTotalCompanies(long totalCompanies) { this.totalCompanies = totalCompanies; }

    public double getOfferAcceptanceRate() { return offerAcceptanceRate; }
    public void setOfferAcceptanceRate(double offerAcceptanceRate) { this.offerAcceptanceRate = offerAcceptanceRate; }

    public Map<String, Long> getPlacementsByBranch() { return placementsByBranch; }
    public void setPlacementsByBranch(Map<String, Long> placementsByBranch) { this.placementsByBranch = placementsByBranch; }

    public List<CompanyOfferDto> getTopHiringCompanies() { return topHiringCompanies; }
    public void setTopHiringCompanies(List<CompanyOfferDto> topHiringCompanies) { this.topHiringCompanies = topHiringCompanies; }

    public Map<String, Long> getMonthlyPlacementTrend() { return monthlyPlacementTrend; }
    public void setMonthlyPlacementTrend(Map<String, Long> monthlyPlacementTrend) { this.monthlyPlacementTrend = monthlyPlacementTrend; }

    public Map<String, Long> getOffersByCtcTier() { return offersByCtcTier; }
    public void setOffersByCtcTier(Map<String, Long> offersByCtcTier) { this.offersByCtcTier = offersByCtcTier; }

    public Map<String, Long> getApplicationPipeline() { return applicationPipeline; }
    public void setApplicationPipeline(Map<String, Long> applicationPipeline) { this.applicationPipeline = applicationPipeline; }

    public List<PlacedStudentDto> getPlacedStudentsList() { return placedStudentsList; }
    public void setPlacedStudentsList(List<PlacedStudentDto> placedStudentsList) { this.placedStudentsList = placedStudentsList; }

    public List<Map<String, String>> getCompaniesList() { return companiesList; }
    public void setCompaniesList(List<Map<String, String>> companiesList) { this.companiesList = companiesList; }

    // Display helpers used in Thymeleaf (avoids logic in templates)
    public double getAverageCtcLpa() { return Math.round((averageCtc / 100000.0) * 10.0) / 10.0; }
    public double getHighestCtcLpa() { return Math.round((highestCtc / 100000.0) * 10.0) / 10.0; }
}
