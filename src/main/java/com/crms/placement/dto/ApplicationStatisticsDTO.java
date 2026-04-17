package com.crms.placement.dto;

public class ApplicationStatisticsDTO {
    private final long totalApplications;
    private final long pendingApplications;
    private final long selectedApplications;
    private final long offerAcceptedApplications;
    private final long offerRejectedApplications;  // ← new
    private final long rejectedApplications;

    public ApplicationStatisticsDTO(long totalApplications,
                                    long pendingApplications,
                                    long selectedApplications,
                                    long offerAcceptedApplications,
                                    long offerRejectedApplications,
                                    long rejectedApplications) {
        this.totalApplications = totalApplications;
        this.pendingApplications = pendingApplications;
        this.selectedApplications = selectedApplications;
        this.offerAcceptedApplications = offerAcceptedApplications;
        this.offerRejectedApplications = offerRejectedApplications;
        this.rejectedApplications = rejectedApplications;
    }

    public long getTotalApplications()         { return totalApplications; }
    public long getPendingApplications()       { return pendingApplications; }
    public long getSelectedApplications()      { return selectedApplications; }
    public long getOfferAcceptedApplications() { return offerAcceptedApplications; }
    public long getOfferRejectedApplications() { return offerRejectedApplications; }
    public long getRejectedApplications()      { return rejectedApplications; }

    // % of all applicants the company shortlisted
    public double getSelectionRate() {
        if (totalApplications == 0) return 0;
        return (selectedApplications * 100.0) / totalApplications;
    }

    // % of selected students who accepted — did the offer land?
    public double getOfferAcceptanceRate() {
        if (selectedApplications == 0) return 0;
        return (offerAcceptedApplications * 100.0) / selectedApplications;
    }

    // % of selected students who declined — useful for company feedback
    public double getOfferRejectionRate() {
        if (selectedApplications == 0) return 0;
        return (offerRejectedApplications * 100.0) / selectedApplications;
    }

    public double getRejectionRate() {
        if (totalApplications == 0) return 0;
        return (rejectedApplications * 100.0) / totalApplications;
    }

    public double getPendingRate() {
        if (totalApplications == 0) return 0;
        return (pendingApplications * 100.0) / totalApplications;
    }
}