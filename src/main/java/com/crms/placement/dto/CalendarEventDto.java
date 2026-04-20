package com.crms.placement.dto;

/**
 * DTO Pattern: Transforms internal entity data into the exact shape FullCalendar.js expects.
 * Decouples the frontend contract from backend entity structure — the controller maps
 * Application / InterviewSlot fields here; the HTML only sees this flat object.
 */
public class CalendarEventDto {

    /** Display label shown on the calendar tile. */
    private String title;

    /** ISO-8601 datetime: "2025-01-15T09:00:00" — FullCalendar parses this directly. */
    private String start;

    /** ISO-8601 datetime for the event end. Null for all-day OA events. */
    private String end;

    /** Hex color string — "#f97316" for OA, "#3b82f6" for interviews. */
    private String color;

    /** "OA" or "INTERVIEW" — used by the frontend modal and upcoming-event cards. */
    private String type;

    /** Company name — shown in the detail modal and upcoming-event cards. */
    private String companyName;

    /** ApplicationStatus or InterviewSlot status as a plain string. */
    private String status;

    public CalendarEventDto() {}

    // --- Getters & Setters ---

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }

    public String getEnd() { return end; }
    public void setEnd(String end) { this.end = end; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
