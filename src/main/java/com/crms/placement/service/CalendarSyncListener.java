package com.crms.placement.service;

import com.crms.placement.event.CalendarUpdateEvent;
import com.crms.placement.model.PendingCalendarUpdate;
import com.crms.placement.repository.PendingCalendarUpdateRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// ============================================================
// OBSERVER PATTERN — ROLE: OBSERVER (Subscriber / Listener)
// ============================================================
// The Observer reacts when the Subject fires an event.
//
// Flow in our system:
//   1. HR dispatches OA   → OADispatchService saves OA to DB
//   2. OADispatchService  → calls CalendarEventPublisher.publishOaAdded()
//   3. CalendarEventPublisher → fires CalendarUpdateEvent into Spring's bus
//   4. Spring sees @EventListener here → calls handleCalendarUpdate()
//   5. This method saves a PendingCalendarUpdate row to DB
//   6. Student's browser polls /api/calendar/updates every 30s
//   7. CalendarController reads undelivered rows → sends them to the browser
//   8. FullCalendar.js re-renders with the new event — live update!
//
// Why not update the calendar directly here?
//   The server cannot push to the browser mid-request (HTTP is stateless).
//   The DB row acts as an async message queue — the browser pulls it when ready.
//
// KEY POINT for presentation:
//   This class does NOT import CalendarEventPublisher.
//   CalendarEventPublisher does NOT import CalendarSyncListener.
//   They are fully decoupled — Spring's @EventListener annotation is the
//   only connection between them. This is the Observer Pattern's core value.
//
// SRP: This class only receives an event and writes one DB row.
//      It does not build DTOs, compute anything, or touch the UI.
// ============================================================
@Component
public class CalendarSyncListener {

    private final PendingCalendarUpdateRepository pendingRepo;

    public CalendarSyncListener(PendingCalendarUpdateRepository pendingRepo) {
        this.pendingRepo = pendingRepo;
    }

    // Spring automatically calls this when CalendarUpdateEvent is published.
    // No registration or wiring code needed — @EventListener handles it.
    @EventListener
    public void handleCalendarUpdate(CalendarUpdateEvent event) {
        // Save the notification to DB so the student's browser can poll and pick it up
        PendingCalendarUpdate update = new PendingCalendarUpdate();
        update.setStudentId(event.getStudentId());
        update.setEventType(event.getEventType());   // "OA_ADDED" or "SLOT_ASSIGNED"
        update.setReferenceId(event.getReferenceId()); // which DB record changed
        update.setCreatedAt(LocalDateTime.now());
        update.setDelivered(false); // will be flipped to true once browser polls it
        pendingRepo.save(update);
    }
}
