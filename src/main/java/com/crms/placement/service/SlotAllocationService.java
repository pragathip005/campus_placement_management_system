package com.crms.placement.service;

// SRP: This service is solely responsible for the slot allocation algorithm.
// Any change to notification or calendar logic does not affect this class.

import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.model.InterviewSlot;
import com.crms.placement.model.Opportunity;
import com.crms.placement.model.Student;
import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.repository.InterviewSlotRepository;
import com.crms.placement.repository.OpportunityRepository;
import com.crms.placement.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SlotAllocationService {

    private final OpportunityRepository opportunityRepository;
    private final StudentRepository studentRepository;
    private final InterviewSlotRepository interviewSlotRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;

    public SlotAllocationService(OpportunityRepository opportunityRepository,
                                  StudentRepository studentRepository,
                                  InterviewSlotRepository interviewSlotRepository,
                                  ApplicationRepository applicationRepository,
                                  NotificationService notificationService) {
        this.opportunityRepository = opportunityRepository;
        this.studentRepository = studentRepository;
        this.interviewSlotRepository = interviewSlotRepository;
        this.applicationRepository = applicationRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public List<InterviewSlot> allocateSlots() {
        List<InterviewSlot> allCreatedSlots = new ArrayList<>();

        // Greedy Strategy: Sort by CTC descending so highest-paying companies get
        // first access to the best students. This is a locally optimal choice at each step.
        List<Opportunity> opportunities = opportunityRepository.findAll()
                .stream()
                .filter(o -> o.getCtc() != null && o.getInterviewDate() != null && o.getVacancies() != null)
                .sorted(Comparator.comparingDouble(Opportunity::getCtc).reversed())
                .toList();

        // Track students already assigned a slot across all opportunities
        Set<Long> assignedStudentIds = new HashSet<>();

        for (Opportunity opportunity : opportunities) {
            // Fetch all applications in INTERVIEW status for this opportunity
            List<Application> interviewApplications = applicationRepository
                    .findByOpportunityIdAndStatus(opportunity.getOpportunity_id(), ApplicationStatus.INTERVIEW);

            if (interviewApplications.isEmpty()) {
                continue;
            }

            double minCgpa = opportunity.getMinCgpa() != null ? opportunity.getMinCgpa() : 0.0;
            int slotsNeeded = opportunity.getVacancies();

            // Load students, apply CGPA filter and deduplicate across companies
            List<Student> eligibleStudents = new ArrayList<>();
            for (Application app : interviewApplications) {
                Optional<Student> studentOpt = studentRepository.findById(app.getStudentId().longValue());
                if (studentOpt.isEmpty()) continue;

                Student student = studentOpt.get();
                double cgpa = student.getCgpa() != null ? student.getCgpa() : 0.0;

                if (cgpa >= minCgpa && !assignedStudentIds.contains(student.getStudentId())) {
                    eligibleStudents.add(student);
                }
            }

            // Sort eligible students by CGPA descending, take top N
            eligibleStudents.sort(Comparator.comparingDouble(
                    s -> -(s.getCgpa() != null ? s.getCgpa() : 0.0)
            ));

            int assignCount = Math.min(slotsNeeded, eligibleStudents.size());

            // Assign time slots starting at 9:00 AM on interviewDate, 30-min intervals
            LocalDateTime baseTime = opportunity.getInterviewDate()
                    .withHour(9)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            for (int i = 0; i < assignCount; i++) {
                Student student = eligibleStudents.get(i);
                LocalDateTime slotTime = baseTime.plusMinutes(30L * i);

                InterviewSlot slot = new InterviewSlot(student, opportunity, slotTime, "SCHEDULED");
                interviewSlotRepository.save(slot);
                allCreatedSlots.add(slot);

                // Mark student as assigned so no other company can claim them
                assignedStudentIds.add(student.getStudentId());

                // Observer Pattern: NotificationService acts as observer,
                // triggered on slot assignment. SlotAllocationService does NOT
                // format or send the notification itself.
                String companyName = opportunity.getCompany() != null
                        ? opportunity.getCompany().getName() : "the company";
                String formattedTime = slotTime.format(
                        DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

                notificationService.notifySlotAssigned(
                        student.getEmail(),
                        student.getName(),
                        companyName,
                        opportunity.getRole(),
                        formattedTime
                );
            }
        }

        return allCreatedSlots;
    }
}
