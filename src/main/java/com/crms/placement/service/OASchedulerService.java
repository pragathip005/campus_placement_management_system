package com.crms.placement.service;

import com.crms.placement.model.*;
import com.crms.placement.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OASchedulerService {

    private final OnlineAssessmentRepository onlineAssessmentRepository;
    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    public OASchedulerService(OnlineAssessmentRepository onlineAssessmentRepository,
                               ApplicationRepository applicationRepository,
                               StudentRepository studentRepository,
                               NotificationService notificationService) {
        this.onlineAssessmentRepository = onlineAssessmentRepository;
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.notificationService = notificationService;
    }

    // ✅ Keep every minute for now so you can verify email arrives
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void sendOAReminders() {
        System.out.println("⏰ Scheduler running: sendOAReminders at " + LocalDateTime.now());

        LocalDate today = LocalDate.now();
        List<OnlineAssessment> allOAs = onlineAssessmentRepository.findAll();

        for (OnlineAssessment oa : allOAs) {
            if (oa.getScheduledAt() == null || oa.isCompleted()) continue;

            LocalDate oaDate = oa.getScheduledAt().toLocalDate();

            if (oaDate.equals(today)) {
                sendOAEmail(oa);
            }

            if (oa.getScheduledAt().isBefore(LocalDateTime.now())) {
                markOACompleted(oa);
            }
        }
    }

    @Transactional
    private void sendOAEmail(OnlineAssessment oa) {
        try {
            // ✅ Skip if already notified
            if (oa.isNotified()) return;

            Application application = oa.getApplication();
            if (application == null) return;

            Student student = studentRepository
                    .findById(application.getStudentId().longValue())
                    .orElse(null);
            if (student == null || student.getEmail() == null) return;

            Opportunity opportunity = application.getOpportunity();
            if (opportunity == null) return;

            Company company = opportunity.getCompany();
            if (company == null || company.getEmail() == null) return;

            String formattedDate = oa.getScheduledAt()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

            notificationService.sendOANotification(
                    student.getEmail(),
                    student.getName(),
                    oa.getOaLink(),
                    formattedDate,
                    company.getName(),
                    company.getEmail()
            );

            // ✅ Mark as notified AFTER sending successfully
            oa.setNotified(true);
            onlineAssessmentRepository.save(oa);

            System.out.println("✅ OA email sent to " + student.getEmail() + " as " + company.getName());

        } catch (Exception e) {
            System.out.println("❌ Error sending OA email: " + e.getMessage());
        }
    }

    @Transactional
    private void markOACompleted(OnlineAssessment oa) {
        try {
            oa.setCompleted(true);

            // ✅ Set dummy score BEFORE saving
            if (oa.getScore() == null) {
               int dummyScore = 60 + new java.util.Random().nextInt(41);
               oa.setScore(dummyScore);
            }

            onlineAssessmentRepository.save(oa); // ✅ single save with both completed=true and score set

            Application application = oa.getApplication();
            if (application != null) {
                application.setStatus(ApplicationStatus.OA_COMPLETED);
                applicationRepository.save(application);

                Student student = studentRepository
                    .findById(application.getStudentId().longValue())
                    .orElse(null);

                Opportunity opportunity = application.getOpportunity();

                if (student != null && opportunity != null && opportunity.getCompany() != null) {
                    notificationService.sendStatusUpdate(
                        student.getEmail(),
                        student.getName(),
                        "OA Completed",
                        opportunity.getCompany().getName(),
                        opportunity.getCompany().getEmail()
                   );
                }
            }
        

            System.out.println("✅ OA marked completed for applicationId="
                + (oa.getApplication() != null ? oa.getApplication().getApplicationId() : "?"));

    } catch (Exception e) {
        System.out.println("❌ Error marking OA completed: " + e.getMessage());
    }
}
}
