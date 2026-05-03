package com.crms.placement.controller;

import com.crms.placement.dto.OADispatchResponseDTO;
import com.crms.placement.service.OADispatchService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oa")
public class OADispatchController {

    private final OADispatchService oaDispatchService;

    public OADispatchController(OADispatchService oaDispatchService) {
        this.oaDispatchService = oaDispatchService;
    }

    /**
     * Trigger OA dispatch for an opportunity
     */
    @PostMapping("/dispatch")
    public OADispatchResponseDTO dispatchOA(@RequestParam Integer opportunityId) {
        return oaDispatchService.dispatchOA(opportunityId);
    }
}