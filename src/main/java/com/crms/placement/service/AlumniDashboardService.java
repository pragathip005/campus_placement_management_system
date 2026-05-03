package com.crms.placement.service;

import com.crms.placement.repository.CareerHistoryRepository;
import com.crms.placement.repository.OAPrepHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AlumniDashboardService {

    private final CareerHistoryRepository careerHistoryRepository;
    private final OAPrepHistoryRepository oaPrepHistoryRepository;

    public AlumniDashboardService(CareerHistoryRepository careerHistoryRepository,
                                  OAPrepHistoryRepository oaPrepHistoryRepository) {
        this.careerHistoryRepository = careerHistoryRepository;
        this.oaPrepHistoryRepository = oaPrepHistoryRepository;
    }

    /** Total career history entries */
    public long getTotalCareerHistory(Long alumniId) {
        return careerHistoryRepository.countByAlumniId(alumniId);
    }

    /** Total OA prep entries */
    public long getTotalOAPrep(Long alumniId) {
        return oaPrepHistoryRepository.countByAlumniId(alumniId);
    }
}