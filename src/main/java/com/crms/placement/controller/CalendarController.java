package com.crms.placement.controller;

import com.crms.placement.dto.CalendarEventDto;
import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.model.InterviewSlot;
import com.crms.placement.model.Opportunity;
import com.crms.placement.model.PendingCalendarUpdate;
import com.crms.placement.model.Student;
import com.crms.placement.model.User;
import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.repository.InterviewSlotRepository;
import com.crms.placement.repository.PendingCalendarUpdateRepository;
import com.crms.placement.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SRP: CalendarController only handles calendar event aggregation and serving the
 * calendar view. It does not modify any application or slot data — purely a
 * read / transform / serve responsibility.
 *
 * Observer Pattern: CalendarController observes ApplicationStatus changes and
 * InterviewSlot assignments to build the student's event feed. It does not own
 * this data — it only reads and transforms it into CalendarEventDtos.
 */
@Controller
public class CalendarController {

    private final ApplicationRepository          applicationRepository;
    private final InterviewSlotRepository        interviewSlotRepository;
    private final StudentRepository              studentRepository;
    private final PendingCalendarUpdateRepository pendingRepo;

    public CalendarController(ApplicationRepository applicationRepository,
                              InterviewSlotRepository interviewSlotRepository,
                              StudentRepository studentRepository,
                              PendingCalendarUpdateRepository pendingRepo) {
        this.applicationRepository   = applicationRepository;
        this.interviewSlotRepository = interviewSlotRepository;
        this.studentRepository       = studentRepository;
        this.pendingRepo             = pendingRepo;
    }

    @GetMapping("/calendar")
    public String calendarPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        Student student = studentRepository.findById(user.getUserId()).orElse(null);
        String username = (student != null) ? student.getName() : user.getName();
        model.addAttribute("username", username);
        return "pages/calendar";
    }

    @GetMapping("/api/calendar/events")
    @ResponseBody
    public List<CalendarEventDto> getEvents(HttpSession session) {
        List<CalendarEventDto> events = new ArrayList<>();
        User user = (User) session.getAttribute("user");
        if (user == null) return events;

        Integer studentIntId = user.getUserId().intValue();
        Long studentLongId = user.getUserId();

        List<ApplicationStatus> oaStatuses = List.of(
                ApplicationStatus.OA_SENT,
                ApplicationStatus.OA_COMPLETED
        );

        List<Application> oaApplications = applicationRepository
                .findByStudentIdAndStatusIn(studentIntId, oaStatuses);

        for (Application app : oaApplications) {
            Opportunity opp = app.getOpportunity();
            if (opp == null || opp.getOaDate() == null) continue;

            String companyName = (opp.getCompany() != null) ? opp.getCompany().getName() : "Company";

            CalendarEventDto dto = new CalendarEventDto();
            dto.setTitle(companyName + " \u2014 Online Assessment");
            dto.setStart(opp.getOaDate().toString());
            dto.setColor("#f97316");
            dto.setType("OA");
            dto.setCompanyName(companyName);
            dto.setStatus(app.getStatus().name());
            events.add(dto);
        }

        List<InterviewSlot> slots = interviewSlotRepository
                .findByStudentIdWithDetails(studentLongId);

        for (InterviewSlot slot : slots) {
            Opportunity opp = slot.getOpportunity();
            if (opp == null || slot.getSlotTime() == null) continue;

            String companyName = (opp.getCompany() != null) ? opp.getCompany().getName() : "Company";

            CalendarEventDto dto = new CalendarEventDto();
            dto.setTitle(companyName + " \u2014 Interview");
            dto.setStart(slot.getSlotTime().toString());
            dto.setEnd(slot.getSlotTime().plusMinutes(30).toString());
            dto.setColor("#3b82f6");
            dto.setType("INTERVIEW");
            dto.setCompanyName(companyName);
            dto.setStatus(slot.getStatus());
            events.add(dto);
        }

        return events;
    }

    @GetMapping("/api/calendar/updates")
    @ResponseBody
    public List<CalendarEventDto> getPendingUpdates(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return List.of();

        List<PendingCalendarUpdate> pending =
                pendingRepo.findByStudentIdAndDeliveredFalse(user.getUserId());

        List<CalendarEventDto> events = pending.stream()
                .map(this::buildEventFromUpdate)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        // Mark all as delivered — same update will not be returned on the next poll
        pending.forEach(u -> u.setDelivered(true));
        pendingRepo.saveAll(pending);

        return events;
    }

    /**
     * Maps a PendingCalendarUpdate to a CalendarEventDto by reloading the referenced
     * entity with a JOIN FETCH so lazy relations are safely accessible.
     * Returns null for unrecognised event types (filtered out by the caller).
     */
    private CalendarEventDto buildEventFromUpdate(PendingCalendarUpdate update) {
        if ("SLOT_ASSIGNED".equals(update.getEventType())) {
            InterviewSlot slot = interviewSlotRepository
                    .findByIdWithDetails(update.getReferenceId())
                    .orElse(null);
            if (slot == null || slot.getSlotTime() == null) return null;

            Opportunity opp = slot.getOpportunity();
            String companyName = (opp != null && opp.getCompany() != null)
                    ? opp.getCompany().getName() : "Company";

            CalendarEventDto dto = new CalendarEventDto();
            dto.setTitle(companyName + " \u2014 Interview");
            dto.setStart(slot.getSlotTime().toString());
            dto.setEnd(slot.getSlotTime().plusMinutes(30).toString());
            dto.setColor("#3b82f6");
            dto.setType("INTERVIEW");
            dto.setCompanyName(companyName);
            dto.setStatus(slot.getStatus());
            return dto;
        }

        if ("OA_ADDED".equals(update.getEventType())) {
            Application app = applicationRepository
                    .findByApplicationIdWithDetails(update.getReferenceId().intValue())
                    .orElse(null);
            if (app == null) return null;

            Opportunity opp = app.getOpportunity();
            if (opp == null || opp.getOaDate() == null) return null;

            String companyName = (opp.getCompany() != null)
                    ? opp.getCompany().getName() : "Company";

            CalendarEventDto dto = new CalendarEventDto();
            dto.setTitle(companyName + " \u2014 Online Assessment");
            dto.setStart(opp.getOaDate().toString());
            dto.setColor("#f97316");
            dto.setType("OA");
            dto.setCompanyName(companyName);
            dto.setStatus(app.getStatus().name());
            return dto;
        }

        return null;
    }
}
