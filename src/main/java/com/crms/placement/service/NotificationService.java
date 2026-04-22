package com.crms.placement.service;

// ============================================================
// OBSERVER PATTERN — ROLE: OBSERVER CONTRACT (Interface)
// also demonstrates: Dependency Inversion Principle (DIP)
// ============================================================
// This interface is the "observer contract" for email notifications.
//
// HOW IT FITS THE OBSERVER PATTERN:
//   Subject   → SlotAllocationService / OASchedulerService / HrDashboardController
//               (they "know" something happened — slot assigned, OA sent, offer given)
//   Observer  → EmailNotificationService (implements this interface)
//               (it "reacts" by sending an email to the student)
//
// HOW IT DEMONSTRATES DIP (Dependency Inversion Principle):
//   SlotAllocationService depends on NotificationService (this interface),
//   NOT on EmailNotificationService (the concrete class).
//   That means we could swap EmailNotificationService for an SMSNotificationService
//   or WhatsAppNotificationService without touching SlotAllocationService at all.
//   → The high-level service does not depend on the low-level detail.
//
// NOTE — Two kinds of Observer in this project:
//   1. Spring @EventListener style → CalendarEventPublisher → CalendarSyncListener
//      (fully decoupled via Spring's event bus)
//   2. Interface-based style → this interface → EmailNotificationService
//      (direct call through interface, decoupled from concrete implementation)
//   Both achieve the same goal: the caller doesn't know WHO handles the reaction.
// ============================================================
public interface NotificationService {

    // Sends OA scheduled email to student — called by OASchedulerService
    void sendOANotification(String toEmail, String studentName,
                             String oaLink, String oaDate,
                             String companyName, String fromEmail);

    // Sends application status change email — called by OASchedulerService
    void sendStatusUpdate(String toEmail, String studentName,
                          String status, String companyName,
                          String fromEmail);

    // Sends interview slot confirmation email — called by SlotAllocationService
    void notifySlotAssigned(String toEmail, String studentName,
                             String companyName, String role,
                             String slotTime);

    // Sends offer letter email — called by HrDashboardController on SELECTED status
    void sendOfferLetter(String toEmail, String studentName,
                            String companyName, String role);

    // Sends rejection email — called by HrDashboardController on REJECTED status
    void sendRejectionEmail(String toEmail, String studentName,
                            String companyName);
}