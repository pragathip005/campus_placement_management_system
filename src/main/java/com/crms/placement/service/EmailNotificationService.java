package com.crms.placement.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
}