package com.crms.placement.dto;

public class PlacedStudentDto {

    private String name;
    private String branch;
    private String companyName;
    private double ctcLpa;

    public PlacedStudentDto() {}

    public PlacedStudentDto(String name, String branch, String companyName, double ctcLpa) {
        this.name = name;
        this.branch = branch;
        this.companyName = companyName;
        this.ctcLpa = ctcLpa;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public double getCtcLpa() { return ctcLpa; }
    public void setCtcLpa(double ctcLpa) { this.ctcLpa = ctcLpa; }
}
