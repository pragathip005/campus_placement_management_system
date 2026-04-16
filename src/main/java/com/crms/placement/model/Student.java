package com.crms.placement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    private String name;

    private String email;

    @Column(unique = true)
    private String srn;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String branch;

    private Integer batchYear;

    private Integer backlogCount;

    private Double sgpaSem1;
    private Double sgpaSem2;
    private Double sgpaSem3;
    private Double sgpaSem4;
    private Double sgpaSem5;
    private Double sgpaSem6;
    private Double sgpaSem7;

    private Double cgpa;

    @Column(columnDefinition = "TEXT")
    private String resumeUrl;

    private Boolean isEligible;

    private Boolean isPlaced;

    @ManyToOne
    @JoinColumn(name = "placed_company_id")
    private Company placedCompany;

    public Student() {}

    // Getters and Setters

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSrn() { return srn; }
    public void setSrn(String srn) { this.srn = srn; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public Integer getBatchYear() { return batchYear; }
    public void setBatchYear(Integer batchYear) { this.batchYear = batchYear; }

    public Integer getBacklogCount() { return backlogCount; }
    public void setBacklogCount(Integer backlogCount) { this.backlogCount = backlogCount; }

    public Double getSgpaSem1() { return sgpaSem1; }
    public void setSgpaSem1(Double sgpaSem1) { this.sgpaSem1 = sgpaSem1; }

    public Double getSgpaSem2() { return sgpaSem2; }
    public void setSgpaSem2(Double sgpaSem2) { this.sgpaSem2 = sgpaSem2; }

    public Double getSgpaSem3() { return sgpaSem3; }
    public void setSgpaSem3(Double sgpaSem3) { this.sgpaSem3 = sgpaSem3; }

    public Double getSgpaSem4() { return sgpaSem4; }
    public void setSgpaSem4(Double sgpaSem4) { this.sgpaSem4 = sgpaSem4; }

    public Double getSgpaSem5() { return sgpaSem5; }
    public void setSgpaSem5(Double sgpaSem5) { this.sgpaSem5 = sgpaSem5; }

    public Double getSgpaSem6() { return sgpaSem6; }
    public void setSgpaSem6(Double sgpaSem6) { this.sgpaSem6 = sgpaSem6; }

    public Double getSgpaSem7() { return sgpaSem7; }
    public void setSgpaSem7(Double sgpaSem7) { this.sgpaSem7 = sgpaSem7; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

    public Boolean getIsEligible() { return isEligible; }
    public void setIsEligible(Boolean isEligible) { this.isEligible = isEligible; }

    public Boolean getIsPlaced() { return isPlaced; }
    public void setIsPlaced(Boolean isPlaced) { this.isPlaced = isPlaced; }

    public Company getPlacedCompany() { return placedCompany; }
    public void setPlacedCompany(Company placedCompany) { this.placedCompany = placedCompany; }
}