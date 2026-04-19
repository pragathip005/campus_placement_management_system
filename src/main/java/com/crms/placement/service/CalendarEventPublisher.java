package com.crms.placement.service;

import com.crms.placement.event.CalendarUpdateEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern — SUBJECT:
 * Fires CalendarUpdateEvent whenever a relevant DB change occurs.
 * Callers (SlotAllocationService, ApplicationService) only call publish*() methods —
 * they have zero knowledge of who is listening or how updates reach the UI.
 *
 * SRP: This class only publishes events. It does not persist, transform, or deliver them.
 */
@Component
public class CalendarEventPublisher {

    private final ApplicationEventPublisher springPublisher;

    public CalendarEventPublisher(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    /** Fire when an application's status changes to OA_SENT. */
    public void publishOaAdded(Long studentId, Long applicationId) {
        springPublisher.publishEvent(
                new CalendarUpdateEvent(this, studentId, "OA_ADDED", applicationId));
    }

    /** Fire after a new InterviewSlot is saved for a student. */
    public void publishSlotAssigned(Long studentId, Long slotId) {
        springPublisher.publishEvent(
                new CalendarUpdateEvent(this, studentId, "SLOT_ASSIGNED", slotId));
    }

    /** Fire when an InterviewSlot status is changed to CANCELLED. */
    public void publishSlotCancelled(Long studentId, Long slotId) {
        springPublisher.publishEvent(
                new CalendarUpdateEvent(this, studentId, "SLOT_CANCELLED", slotId));
    }
}
