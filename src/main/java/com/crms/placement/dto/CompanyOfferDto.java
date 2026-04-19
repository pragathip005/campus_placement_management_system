package com.crms.placement.dto;

// DTO Pattern: Decouples API response from internal entity models.
// Controllers never expose raw Company or Application entities.
public class CompanyOfferDto {

    private String companyName;
    private long offerCount;
    private double avgCtc;     // raw value from DB (divide by 100000 for LPA display)
    private String tier;       // "Dream", "Tier 1", "Tier 2", "Tier 3"

    public CompanyOfferDto() {}

    public CompanyOfferDto(String companyName, long offerCount, double avgCtc, String tier) {
        this.companyName = companyName;
        this.offerCount = offerCount;
        this.avgCtc = avgCtc;
        this.tier = tier;
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public long getOfferCount() { return offerCount; }
    public void setOfferCount(long offerCount) { this.offerCount = offerCount; }

    public double getAvgCtc() { return avgCtc; }
    public void setAvgCtc(double avgCtc) { this.avgCtc = avgCtc; }

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }

    // Convenience: display-ready LPA value (assumes DB stores CTC in full rupees)
    public double getAvgCtcLpa() {
        return Math.round((avgCtc / 100000.0) * 10.0) / 10.0;
    }
}
