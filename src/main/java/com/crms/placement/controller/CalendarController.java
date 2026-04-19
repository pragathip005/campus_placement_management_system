package com.crms.placement.controller;

import com.crms.placement.dto.CalendarEventDto;
import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.model.InterviewSlot;
import com.crms.placement.model.Opportunity;
import com.crms.placement.model.Student;
import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.repository.InterviewSlotRepository;
import com.crms.placement.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

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

    private static final Integer HARDCODED_STUDENT_ID = 10;
    private static final Long    HARDCODED_STUDENT_LONG_ID = 1L;

    private final ApplicationRepository   applicationRepository;
    private final InterviewSlotRepository interviewSlotRepository;
    private final StudentRepository       studentRepository;

    public CalendarController(ApplicationRepository applicationRepository,
                              InterviewSlotRepository interviewSlotRepository,
                              StudentRepository studentRepository) {
        this.applicationRepository   = applicationRepository;
        this.interviewSlotRepository = interviewSlotRepository;
        this.studentRepository       = studentRepository;
    }

    /**
     * Serves the calendar Thymeleaf page.
     * Title "Calendar" activates the sidebar's Calendar nav item automatically
     * (the layout fragment passes title as activePage to the sidebar).
     */
    @GetMapping("/calendar")
    public String calendarPage(Model model) {
        Student student = studentRepository.findById(HARDCODED_STUDENT_LONG_ID).orElse(null);
        String username = (student != null) ? student.getName() : "Student";
        model.addAttribute("username", username);
        return "pages/calendar";
    }

    /**
     * REST endpoint — FullCalendar.js fetches events from here via:
     *   events: '/api/calendar/events'
     *
     * Returns a flat list of CalendarEventDtos (serialised to JSON by Jackson).
     * Two event types are built:
     *   OA events   — sourced from Application rows where status IN (OA_SENT, OA_COMPLETED)
     *   Interview   — sourced from InterviewSlot rows for this student
     */
    @GetMapping("/api/calendar/events")
    @ResponseBody
    public List<CalendarEventDto> getEvents() {
        List<CalendarEventDto> events = new ArrayList<>();

        // ── Type 1: Online Assessment events ─────────────────────────────────────
        // OA date lives on the Opportunity, not the Application.
        // The JPQL query JOIN FETCHes opportunity + company to avoid LazyInitializationException.
        List<ApplicationStatus> oaStatuses = List.of(
                ApplicationStatus.OA_SENT,
                ApplicationStatus.OA_COMPLETED
        );

        List<Application> oaApplications = applicationRepository
                .findByStudentIdAndStatusIn(HARDCODED_STUDENT_ID, oaStatuses);

        for (Application app : oaApplications) {
            Opportunity opp = app.getOpportunity();
            if (opp == null || opp.getOaDate() == null) continue;

            String companyName = (opp.getCompany() != null) ? opp.getCompany().getName() : "Company";

            CalendarEventDto dto = new CalendarEventDto();
            dto.setTitle(companyName + " \u2014 Online Assessment");
            dto.setStart(opp.getOaDate().toString());
            // OA events are point-in-time (no explicit end); FullCalendar renders them as all-day if no end.
            dto.setColor("#f97316");
            dto.setType("OA");
            dto.setCompanyName(companyName);
            dto.setStatus(app.getStatus().name());
            events.add(dto);
        }

        // ── Type 2: Interview Slot events ────────────────────────────────────────
        // The JPQL query JOIN FETCHes opportunity + company for safe access.
        List<InterviewSlot> slots = interviewSlotRepository
                .findByStudentIdWithDetails(HARDCODED_STUDENT_LONG_ID);

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
}
