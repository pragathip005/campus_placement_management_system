package com.crms.placement.service;

import com.crms.placement.event.CalendarUpdateEvent;
import com.crms.placement.model.PendingCalendarUpdate;
import com.crms.placement.repository.PendingCalendarUpdateRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Observer Pattern — OBSERVER:
 * Listens for CalendarUpdateEvent and persists it as a PendingCalendarUpdate row
 * so the student's browser can pick it up on the next poll.
 *
 * This class has zero knowledge of who published the event (CalendarEventPublisher).
 * Spring's @EventListener wires the two together without direct coupling.
 *
 * SRP: Only responsibility is receiving a calendar event and persisting the pending
 * update row. It does not build DTOs, send emails, or touch the calendar UI.
 */
@Component
public class CalendarSyncListener {

    private final PendingCalendarUpdateRepository pendingRepo;

    public CalendarSyncListener(PendingCalendarUpdateRepository pendingRepo) {
        this.pendingRepo = pendingRepo;
    }

    @EventListener
    public void handleCalendarUpdate(CalendarUpdateEvent event) {
        PendingCalendarUpdate update = new PendingCalendarUpdate();
        update.setStudentId(event.getStudentId());
        update.setEventType(event.getEventType());
        update.setReferenceId(event.getReferenceId());
        update.setCreatedAt(LocalDateTime.now());
        update.setDelivered(false);
        pendingRepo.save(update);
    }
}
