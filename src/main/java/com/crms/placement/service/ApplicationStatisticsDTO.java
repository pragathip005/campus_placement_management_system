package com.crms.placement.service;

/**
 * Data Transfer Object for application statistics
 * Represents statistics snapshot for an opportunity
 */
public class ApplicationStatisticsDTO {
    private final long totalApplications;
    private final long acceptedApplications;
    private final long rejectedApplications;
    private final long pendingApplications;

    public ApplicationStatisticsDTO(long totalApplications, long acceptedApplications,
                                  long rejectedApplications, long pendingApplications) {
        this.totalApplications = totalApplications;
        this.acceptedApplications = acceptedApplications;
        this.rejectedApplications = rejectedApplications;
        this.pendingApplications = pendingApplications;
    }

    public long getTotalApplications() { return totalApplications; }
    public long getAcceptedApplications() { return acceptedApplications; }
    public long getRejectedApplications() { return rejectedApplications; }
    public long getPendingApplications() { return pendingApplications; }

    public double getAcceptanceRate() {
        if (totalApplications == 0) return 0;
        return (acceptedApplications * 100.0) / totalApplications;
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
