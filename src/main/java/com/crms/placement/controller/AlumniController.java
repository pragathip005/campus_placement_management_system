package com.crms.placement.controller;

import com.crms.placement.dto.AlumniDetailsDto;
import com.crms.placement.entity.Alumni;
import com.crms.placement.entity.CareerHistory;
import com.crms.placement.entity.OAPrepHistory;
import com.crms.placement.service.AlumniService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/alumni")
public class AlumniController {

    private final AlumniService alumniService;

    public AlumniController(AlumniService alumniService) {
        this.alumniService = alumniService;
    }

    @PostMapping
    public ResponseEntity<Alumni> createAlumni(@RequestBody Alumni alumni) {
        Alumni saved = alumniService.saveAlumni(alumni);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/{alumniId}/career")
    public ResponseEntity<CareerHistory> addCareerHistory(
            @PathVariable Long alumniId,
            @RequestBody CareerHistory careerHistory) {
        careerHistory.setAlumniId(alumniId);
        CareerHistory saved = alumniService.saveCareerHistory(careerHistory);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/{alumniId}/prep")
    public ResponseEntity<OAPrepHistory> addOAPrepHistory(
            @PathVariable Long alumniId,
            @RequestBody OAPrepHistory prepHistory) {
        prepHistory.setAlumniId(alumniId);
        OAPrepHistory saved = alumniService.saveOAPrepHistory(prepHistory);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Alumni> getAllAlumni() {
        return alumniService.getAllAlumni();
    }

    @GetMapping("/{alumniId}")
    public Alumni getAlumni(@PathVariable Long alumniId) {
        return alumniService.getAlumniById(alumniId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumni not found"));
    }

    @GetMapping("/{alumniId}/details")
    public AlumniDetailsDto getAlumniDetails(@PathVariable Long alumniId) {
        try {
            return alumniService.getAlumniDetails(alumniId);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
