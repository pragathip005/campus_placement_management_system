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

    // rest of the methods unchanged...
}