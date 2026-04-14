package com.crms.placement.service;

/**
 * Value Object for eligibility result
 * Encapsulates eligibility decision with detailed reason
 *
 * Benefits:
 * - Type-safe return value instead of boolean
 * - Provides detailed reason for ineligibility
 * - Makes code self-documenting
 * - Follows Value Object pattern
 */
public class EligibilityResult {
    private final boolean eligible;
    private final String reason;

    private EligibilityResult(boolean eligible, String reason) {
        this.eligible = eligible;
        this.reason = reason;
    }

    public boolean isEligible() {
        return eligible;
    }

    public String getReason() {
        return reason;
    }

    public static EligibilityResult eligible() {
        return new EligibilityResult(true, null);
    }

    public static EligibilityResult ineligible(String reason) {
        return new EligibilityResult(false, reason);
    }

    @Override
    public String toString() {
        return eligible ? "Eligible" : "Ineligible: " + reason;
    }
}
