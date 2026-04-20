package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stores undelivered calendar update notifications so the student's browser
 * can poll and pick them up. Once delivered, the row is kept but marked
 * delivered=true — we never delete records (audit trail).
 */
@Entity
@Table(name = "pending_calendar_updates")
public class PendingCalendarUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    /** "OA_ADDED" | "SLOT_ASSIGNED" | "SLOT_CANCELLED" */
    @Column(nullable = false)
    private String eventType;

    /** applicationId (OA_ADDED) or interviewSlotId (SLOT_ASSIGNED / SLOT_CANCELLED). */
    @Column(nullable = false)
    private Long referenceId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** True once the student's browser has polled and received this update. */
    @Column(nullable = false)
    private boolean delivered;

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isDelivered() { return delivered; }
    public void setDelivered(boolean delivered) { this.delivered = delivered; }
}
