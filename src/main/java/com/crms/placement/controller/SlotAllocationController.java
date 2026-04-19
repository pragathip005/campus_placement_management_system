package com.crms.placement.controller;

import com.crms.placement.model.InterviewSlot;
import com.crms.placement.repository.InterviewSlotRepository;
import com.crms.placement.service.SlotAllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class SlotAllocationController {

    private final SlotAllocationService slotAllocationService;
    private final InterviewSlotRepository interviewSlotRepository;

    public SlotAllocationController(SlotAllocationService slotAllocationService,
                                     InterviewSlotRepository interviewSlotRepository) {
        this.slotAllocationService = slotAllocationService;
        this.interviewSlotRepository = interviewSlotRepository;
    }

    // Admin triggers the full greedy slot allocation run
    @PostMapping("/api/admin/slots/allocate")
    public ResponseEntity<Map<String, Object>> allocateSlots() {
        List<InterviewSlot> created = slotAllocationService.allocateSlots();
        return ResponseEntity.ok(Map.of(
                "message", "Slot allocation completed",
                "slotsCreated", created.size()
        ));
    }

    // Student fetches their own assigned interview slot by studentId
    @GetMapping("/api/slots/my-slot")
    public ResponseEntity<?> getMySlot(@RequestParam Long studentId) {
        Optional<InterviewSlot> slot = interviewSlotRepository.findByStudent_StudentId(studentId);

        if (slot.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No interview slot assigned yet"));
        }

        InterviewSlot s = slot.get();
        String companyName = s.getOpportunity().getCompany() != null
                ? s.getOpportunity().getCompany().getName() : "N/A";

        return ResponseEntity.ok(Map.of(
                "company", companyName,
                "role", s.getOpportunity().getRole() != null ? s.getOpportunity().getRole() : "N/A",
                "slotTime", s.getSlotTime().toString(),
                "status", s.getStatus()
        ));
    }
}
