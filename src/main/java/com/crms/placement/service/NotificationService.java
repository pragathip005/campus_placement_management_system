package com.crms.placement.service;

public interface NotificationService {

    /**
     * Send OA notification to student
     * @param toEmail      student's email
     * @param studentName  student's name
     * @param oaLink       the OA link
     * @param oaDate       formatted date string
     * @param companyName  company name
     * @param fromEmail    company's email (sender)
     */
    void sendOANotification(String toEmail, String studentName,
                             String oaLink, String oaDate,
                             String companyName, String fromEmail);

    /**
     * Send status update to student
     * @param toEmail     student's email
     * @param studentName student's name
     * @param status      new application status
     * @param companyName company name
     * @param fromEmail   company's email (sender)
     */
    void sendStatusUpdate(String toEmail, String studentName,
                          String status, String companyName,
                          String fromEmail);

    /**
     * Observer Pattern: Notify student of their assigned interview slot.
     * Called by SlotAllocationService; this service acts as the observer.
     *
     * @param toEmail      student's email
     * @param studentName  student's name
     * @param companyName  company name
     * @param role         job role
     * @param slotTime     formatted slot date/time string
     */
    void notifySlotAssigned(String toEmail, String studentName,
                             String companyName, String role,
                             String slotTime);

    void sendOfferLetter(String toEmail, String studentName,
                            String companyName, String role);

    void sendRejectionEmail(String toEmail, String studentName,
                            String companyName);
}