package com.crms.placement.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

// ============================================================
// OBSERVER PATTERN — ROLE: CONCRETE OBSERVER (Email)
// ============================================================
// EmailNotificationService IS the observer that reacts to placement events
// by sending emails to students.
//
// It implements NotificationService (the observer contract/interface).
// The subjects (SlotAllocationService, OASchedulerService, HrDashboardController)
// call methods on the interface — they have zero knowledge that it's email
// specifically. You could replace this with SMS and nothing else changes.
//
// Emails sent:
//   • OA scheduled         → sendOANotification()    (from OASchedulerService)
//   • Status changed       → sendStatusUpdate()      (from OASchedulerService)
//   • Interview slot given → notifySlotAssigned()    (from SlotAllocationService)
//   • Offer letter         → sendOfferLetter()       (from HrDashboardController)
//   • Rejection            → sendRejectionEmail()    (from HrDashboardController)
// ============================================================
@Service
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOANotification(String toEmail, String studentName,
                                    String oaLink, String oaDate,
                                    String companyName, String fromEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(senderEmail, companyName + " Recruitment"); // ✅ relay email, company display name
            helper.setTo(toEmail);
            helper.setSubject("Online Assessment Scheduled - " + companyName);
            helper.setText(buildOAEmailBody(studentName, companyName, oaDate, oaLink), true);

            mailSender.send(message);
            System.out.println("✅ OA email sent to " + toEmail + " as " + companyName);

        } catch (Exception e) {
            System.out.println("❌ Failed to send OA email to " + toEmail + ": " + e.getMessage());
        }
    }

    @Override
    public void sendStatusUpdate(String toEmail, String studentName,
                                  String status, String companyName,
                                  String fromEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(senderEmail, companyName + " Recruitment"); // ✅ relay email, company display name
            helper.setTo(toEmail);
            helper.setSubject("Application Status Update - " + companyName);
            helper.setText(buildStatusEmailBody(studentName, companyName, status), true);

            mailSender.send(message);
            System.out.println("✅ Status update email sent to " + toEmail + " as " + companyName);

        } catch (Exception e) {
            System.out.println("❌ Failed to send status email to " + toEmail + ": " + e.getMessage());
        }
    }

    private String buildOAEmailBody(String studentName, String companyName,
                                     String oaDate, String oaLink) {
        return """
            <html>
            <body>
                <p>Dear %s,</p>
                <p>Your Online Assessment for <strong>%s</strong> is scheduled on <strong>%s</strong>.</p>
                <p>Please click the link below to attempt your OA:</p>
                <p><a href="%s">%s</a></p>
                <br/>
                <p>Best of luck!</p>
                <p><strong>%s Recruitment Team</strong></p>
            </body>
            </html>
            """.formatted(studentName, companyName, oaDate, oaLink, oaLink, companyName);
    }

    @Override
    public void notifySlotAssigned(String toEmail, String studentName,
                                    String companyName, String role,
                                    String slotTime) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(senderEmail, companyName + " Recruitment");
            helper.setTo(toEmail);
            helper.setSubject("Interview Slot Confirmed - " + companyName);
            helper.setText(buildSlotEmailBody(studentName, companyName, role, slotTime), true);

            mailSender.send(message);
            System.out.println("✅ Slot notification sent to " + toEmail);

        } catch (Exception e) {
            System.out.println("❌ Failed to send slot email to " + toEmail + ": " + e.getMessage());
        }
    }

    private String buildStatusEmailBody(String studentName, String companyName, String status) {
        return """
            <html>
            <body>
                <p>Dear %s,</p>
                <p>Your application status for <strong>%s</strong> has been updated to: <strong>%s</strong>.</p>
                <p>Please log in to the placement portal for more details.</p>
                <br/>
                <p><strong>%s Recruitment Team</strong></p>
            </body>
            </html>
            """.formatted(studentName, companyName, status, companyName);
    }

    private String buildSlotEmailBody(String studentName, String companyName,
                                       String role, String slotTime) {
        return """
            <html>
            <body>
                <p>Dear %s,</p>
                <p>Congratulations! You have been shortlisted for an interview at <strong>%s</strong>.</p>
                <p><strong>Role:</strong> %s</p>
                <p><strong>Interview Slot:</strong> %s</p>
                <p>Please be present 15 minutes before your scheduled time. Carry a printed copy of your resume.</p>
                <br/>
                <p>Best of luck!</p>
                <p><strong>%s Recruitment Team</strong></p>
            </body>
            </html>
            """.formatted(studentName, companyName, role, slotTime, companyName);
    }


    @Override
public void sendOfferLetter(String toEmail, String studentName,
                            String companyName, String role) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(senderEmail, companyName + " Recruitment");
        helper.setTo(toEmail);
        helper.setSubject("🎉 Offer Letter - " + companyName);
        helper.setText(buildOfferEmailBody(studentName, companyName, role), true);

        mailSender.send(message);
        System.out.println("✅ Offer email sent to " + toEmail);

    } catch (Exception e) {
        System.out.println("❌ Failed to send offer email: " + e.getMessage());
    }
}


    @Override
public void sendRejectionEmail(String toEmail, String studentName,
                                String companyName) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(senderEmail, companyName + " Recruitment");
        helper.setTo(toEmail);
        helper.setSubject("Application Update - " + companyName);
        helper.setText(buildRejectionEmailBody(studentName, companyName), true);

        mailSender.send(message);
        System.out.println("✅ Rejection email sent to " + toEmail);

    } catch (Exception e) {
        System.out.println("❌ Failed to send rejection email: " + e.getMessage());
    }
}


private String buildOfferEmailBody(String studentName, String companyName, String role) {
    return """
        <html>
        <body>
            <p>Dear %s,</p>
            <p>🎉 Congratulations!</p>
            <p>You have been selected for the role of <strong>%s</strong> at <strong>%s</strong>.</p>
            <p>Please log in to the portal to accept or reject your offer.</p>
            <br/>
            <p><strong>%s Recruitment Team</strong></p>
        </body>
        </html>
        """.formatted(studentName, role, companyName, companyName);
}



      private String buildRejectionEmailBody(String studentName, String companyName) {
    return """
        <html>
        <body>
            <p>Dear %s,</p>
            <p>Thank you for applying to <strong>%s</strong>.</p>
            <p>We regret to inform you that you were not selected for this role.</p>
            <p>We wish you all the best for your future.</p>
            <br/>
            <p><strong>%s Recruitment Team</strong></p>
        </body>
        </html>
        """.formatted(studentName, companyName, companyName);
}
}