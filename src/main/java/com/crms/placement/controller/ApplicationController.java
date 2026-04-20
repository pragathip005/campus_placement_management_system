package com.crms.placement.controller;

import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.service.ApplicationManager;  // ADD
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final ApplicationManager applicationManager;  // ADD

    public ApplicationController(ApplicationRepository applicationRepository,
                                  ApplicationManager applicationManager) {  // ADD
        this.applicationRepository = applicationRepository;
        this.applicationManager = applicationManager;  // ADD
    }

    // ✅ Apply for a job — now uses ApplicationManager (duplicate check included)
    @PostMapping("/apply")  // GET → POST
    public Application apply(
            @RequestParam Integer studentId,
            @RequestParam Integer opportunityId
    ) {
        return applicationManager.submitApplication(studentId, opportunityId);  // replaces raw repo call
    }

    // ✅ Get all applications
    @GetMapping
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    // ✅ Get applications by student
    @GetMapping("/student/{studentId}")
    public List<Application> getByStudent(@PathVariable Integer studentId) {
        return applicationRepository.findAll()
                .stream()
                .filter(a -> a.getStudentId().equals(studentId))
                .toList();
    }

    // ✅ Update status
    @PutMapping("/{id}/status")
    public Application updateStatus(
            @PathVariable Integer id,
            @RequestParam ApplicationStatus status
    ) {
        Optional<Application> optional = applicationRepository.findById(id);

        if (optional.isEmpty()) {
            throw new RuntimeException("Application not found");
        }

        Application application = optional.get();
        application.setStatus(status);

        return applicationRepository.save(application);
    }
}