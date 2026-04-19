package com.crms.placement.dto;

import lombok.Data;

@Data
public class RegistrationRequestDTO {
    private String role;
    private String name;
    private String email;
    private String password;
    
    // Student fields
    private String srn;
    private String branch;
    private Integer batchYear;
    private Double cgpa;
    private Integer backlogCount;
    private Double sgpaSem1;
    private Double sgpaSem2;
    private Double sgpaSem3;
    private Double sgpaSem4;
    private Double sgpaSem5;
    private Double sgpaSem6;
    private Double sgpaSem7;
    private String phone;
    private String address;
    private String resumeUrl;

    // HR fields
    private String companyName;
    private String companyEmail;
    private String companyPassword;
    private String industry;

    // Alumni fields
    private Integer graduationYear;
    private String currentCompany;
    private String currentJobRole;
    private String placementStatus;
    private String linkedinUrl;
    private String companyHistory;
}
