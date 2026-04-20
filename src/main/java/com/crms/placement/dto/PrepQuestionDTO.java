package com.crms.placement.dto;

import java.time.LocalDateTime;

public class PrepQuestionDTO {

    private String title;
    private String description;
    private String difficultyLevel;  // ✅ replaces batch
    private LocalDateTime addedDate;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public LocalDateTime getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDateTime addedDate) { this.addedDate = addedDate; }
}