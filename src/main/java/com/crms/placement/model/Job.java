package com.crms.placement.model;

public class Job {

    private int id;
    private String title;
    private String company;
    private String location;
    private String roleType;
    private String ctc;
    private double cgpa;
    private String deadline;
    private String description;
    private boolean applied;
    private boolean eligible;

    // Constructor
    public Job(int id, String title, String company, String location,
               String roleType, String ctc, double cgpa,
               String deadline, String description,
               boolean applied, boolean eligible) {

        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.roleType = roleType;
        this.ctc = ctc;
        this.cgpa = cgpa;
        this.deadline = deadline;
        this.description = description;
        this.applied = applied;
        this.eligible = eligible;
    }

    // Getters (REQUIRED for Thymeleaf)
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getRoleType() { return roleType; }
    public String getCtc() { return ctc; }
    public double getCgpa() { return cgpa; }
    public String getDeadline() { return deadline; }
    public String getDescription() { return description; }
    public boolean isApplied() { return applied; }
    public boolean isEligible() { return eligible; }
}
