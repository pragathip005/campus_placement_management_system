package com.crms.placement.event;

import org.springframework.context.ApplicationEvent;

/**
 * Observer Pattern — EVENT object:
 * Carries what changed (eventType), for whom (studentId), and which record (referenceId).
 * Neither the publisher nor the observer know about each other — only this event class
 * is shared between them. Spring's event bus is the decoupling mechanism.
 */
public class CalendarUpdateEvent extends ApplicationEvent {

    private final Long   studentId;
    private final String eventType;   // "OA_ADDED" | "SLOT_ASSIGNED" | "SLOT_CANCELLED"
    private final Long   referenceId; // applicationId or interviewSlotId

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
