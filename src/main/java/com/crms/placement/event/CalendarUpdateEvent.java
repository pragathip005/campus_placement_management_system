package com.crms.placement.event;

import org.springframework.context.ApplicationEvent;

// ============================================================
// OBSERVER PATTERN — ROLE: EVENT (Message / Notification)
// ============================================================
// In the Observer Pattern there are three roles:
//   1. Subject   → CalendarEventPublisher  (fires events)
//   2. Event     → CalendarUpdateEvent     (this class — the message)
//   3. Observer  → CalendarSyncListener    (reacts to the event)
//
// This class is the "message envelope" passed from Subject to Observer.
// It carries three pieces of information:
//   • WHO  is affected  → studentId
//   • WHAT happened     → eventType ("OA_ADDED", "SLOT_ASSIGNED", "SLOT_CANCELLED")
//   • WHICH record      → referenceId (applicationId or interviewSlotId)
//
// Key design benefit: CalendarEventPublisher and CalendarSyncListener
// never import each other — they are fully decoupled.  The only shared
// contract between them is this event class.
// ============================================================
public class CalendarUpdateEvent extends ApplicationEvent {

    private final Long   studentId;
    private final String eventType;   // "OA_ADDED" | "SLOT_ASSIGNED" | "SLOT_CANCELLED"
    private final Long   referenceId; // applicationId (OA_ADDED) or interviewSlotId (SLOT_*)

    public CalendarUpdateEvent(Object source, Long studentId,
                                String eventType, Long referenceId) {
        super(source);
        this.studentId   = studentId;
        this.eventType   = eventType;
        this.referenceId = referenceId;
    }

    public Long   getStudentId()   { return studentId;   }
    public String getEventType()   { return eventType;   }
    public Long   getReferenceId() { return referenceId; }
}
