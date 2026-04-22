package com.crms.placement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ============================================================
// OBSERVER PATTERN — ROLE: MESSAGE QUEUE (DB-backed delivery)
// ============================================================
// Because HTTP is stateless, the server cannot push an update to
// the browser directly. Instead, CalendarSyncListener writes a row
// here, and the browser polls /api/calendar/updates every 30 seconds
// to collect undelivered rows.
//
// This acts as a lightweight async message queue stored in the DB.
// Once the browser picks up a row, CalendarController sets
// delivered=true so the same update is never returned twice.
//
// Fields:
//   studentId   — which student's calendar should update
//   eventType   — "OA_ADDED" | "SLOT_ASSIGNED" | "SLOT_CANCELLED"
//   referenceId — the DB primary key of the changed entity
//   delivered   — false = pending, true = already sent to browser
// ============================================================
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
