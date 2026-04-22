package com.crms.placement.service;

import com.crms.placement.event.CalendarUpdateEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

// ============================================================
// OBSERVER PATTERN — ROLE: SUBJECT (Publisher / Observable)
// ============================================================
// The Subject is the one who "knows something happened" and
// notifies all registered observers.
//
// In our system, placement-related actions (OA dispatched,
// interview slot assigned) happen deep inside service classes.
// Instead of those services directly updating the calendar,
// they simply call one method here — and CalendarEventPublisher
// fires the event into Spring's internal event bus.
//
// WHY this matters:
//   • OADispatchService doesn't need to know the calendar exists.
//   • SlotAllocationService doesn't need to know the calendar exists.
//   • Adding a new observer (e.g., email notification) only requires
//     adding a new @EventListener — zero changes to existing code.
//     → This is the Open/Closed Principle (OCP) in action.
//
// SRP: This class has exactly one job — publish events.
//      It does NOT persist data, send emails, or update the UI.
// ============================================================
@Component
public class CalendarEventPublisher {

    // Spring's built-in event bus — injected automatically
    private final ApplicationEventPublisher springPublisher;

    public CalendarEventPublisher(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    // Called by OADispatchService after setting application status to OA_SENT
    public void publishOaAdded(Long studentId, Long applicationId) {
        springPublisher.publishEvent(
                new CalendarUpdateEvent(this, studentId, "OA_ADDED", applicationId));
    }

    // Called by SlotAllocationService after saving a new InterviewSlot
    public void publishSlotAssigned(Long studentId, Long slotId) {
        springPublisher.publishEvent(
                new CalendarUpdateEvent(this, studentId, "SLOT_ASSIGNED", slotId));
    }

    // Called when an InterviewSlot is cancelled by HR
    public void publishSlotCancelled(Long studentId, Long slotId) {
        springPublisher.publishEvent(
                new CalendarUpdateEvent(this, studentId, "SLOT_CANCELLED", slotId));
    }
}
