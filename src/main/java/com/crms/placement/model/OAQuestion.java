package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "oa_questions")
public class OAQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String batch;

    @Column(name = "added_date")
    private LocalDateTime addedDate;

    // Constructors
    public OAQuestion() {}

    public OAQuestion(Company company, String title, String description, String batch, LocalDateTime addedDate) {
        this.company = company;
        this.title = title;
        this.description = description;
        this.batch = batch;
        this.addedDate = addedDate;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public LocalDateTime getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDateTime addedDate) { this.addedDate = addedDate; }
}
