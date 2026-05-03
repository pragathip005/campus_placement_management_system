package com.crms.placement.service;

import com.crms.placement.dto.AlumniDetailsDto;
import com.crms.placement.entity.Alumni;
import com.crms.placement.entity.CareerHistory;
import com.crms.placement.entity.OAPrepHistory;
import com.crms.placement.repository.AlumniRepository;
import com.crms.placement.repository.CareerHistoryRepository;
import com.crms.placement.repository.OAPrepHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlumniService {

    private final AlumniRepository alumniRepository;
    private final CareerHistoryRepository careerHistoryRepository;
    private final OAPrepHistoryRepository oaPrepHistoryRepository;

    @Autowired
    public AlumniService(AlumniRepository alumniRepository,
                         CareerHistoryRepository careerHistoryRepository,
                         OAPrepHistoryRepository oaPrepHistoryRepository) {
        this.alumniRepository = alumniRepository;
        this.careerHistoryRepository = careerHistoryRepository;
        this.oaPrepHistoryRepository = oaPrepHistoryRepository;
    }

    public Alumni saveAlumni(Alumni alumni) {
        return alumniRepository.save(alumni);
    }

    public CareerHistory saveCareerHistory(CareerHistory careerHistory) {
        return careerHistoryRepository.save(careerHistory);
    }

    public OAPrepHistory saveOAPrepHistory(OAPrepHistory prepHistory) {
        return oaPrepHistoryRepository.save(prepHistory);
    }

    public List<Alumni> getAllAlumni() {
        return alumniRepository.findAll();
    }

    public Optional<Alumni> getAlumniById(Long alumniId) {
        return alumniRepository.findById(alumniId);
    }

    public List<CareerHistory> getCareerHistoryForAlumni(Long alumniId) {
        return careerHistoryRepository.findByAlumniId(alumniId);
    }

    public List<OAPrepHistory> getOAPrepHistoryForAlumni(Long alumniId) {
        return oaPrepHistoryRepository.findByAlumniId(alumniId);
    }

    public AlumniDetailsDto getAlumniDetails(Long alumniId) {
        Alumni alumni = getAlumniById(alumniId)
                .orElseThrow(() -> new IllegalArgumentException("Alumni not found: " + alumniId));

        return new AlumniDetailsDto(
                alumni,
                getCareerHistoryForAlumni(alumniId),
                getOAPrepHistoryForAlumni(alumniId)
        );
    }
}
