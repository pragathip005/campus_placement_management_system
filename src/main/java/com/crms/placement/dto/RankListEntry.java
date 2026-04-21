package com.crms.placement.dto;

/**
 * DTO representing a single entry in the rank list.
 */
public class RankListEntry {

    private Integer applicationId;
    private String studentName;
    private String email;
    private Double cgpa;
    private Integer oaScore;
    private Double compositeScore;
    private Integer rank;
    private String branch;
    private String srn;

    public RankListEntry() {}

    public RankListEntry(Integer applicationId, String studentName, String email,
                         Double cgpa, Integer oaScore, String branch, String srn) {
        this.applicationId = applicationId;
        this.studentName = studentName;
        this.email = email;
        this.cgpa = cgpa != null ? cgpa : 0.0;
        this.oaScore = oaScore != null ? oaScore : 0;
        this.compositeScore = 0.0;
        this.branch = branch;
        this.srn = srn;
    }

    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public Integer getOaScore() { return oaScore; }
    public void setOaScore(Integer oaScore) { this.oaScore = oaScore; }

    public Double getCompositeScore() { return compositeScore; }
    public void setCompositeScore(Double compositeScore) { this.compositeScore = compositeScore; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getSrn() { return srn; }
    public void setSrn(String srn) { this.srn = srn; }
}