package com.crms.placement.dto;

import com.crms.placement.entity.Alumni;
import com.crms.placement.entity.CareerHistory;
import com.crms.placement.entity.OAPrepHistory;

import java.util.List;

public class AlumniDetailsDto {

    private Alumni alumni;
    private List<CareerHistory> careerHistory;
    private List<OAPrepHistory> oaPrepHistory;

    public AlumniDetailsDto() {
    }

    public AlumniDetailsDto(Alumni alumni, List<CareerHistory> careerHistory, List<OAPrepHistory> oaPrepHistory) {
        this.alumni = alumni;
        this.careerHistory = careerHistory;
        this.oaPrepHistory = oaPrepHistory;
    }

    public Alumni getAlumni() {
        return alumni;
    }

    public void setAlumni(Alumni alumni) {
        this.alumni = alumni;
    }

    public List<CareerHistory> getCareerHistory() {
        return careerHistory;
    }

    public void setCareerHistory(List<CareerHistory> careerHistory) {
        this.careerHistory = careerHistory;
    }

    public List<OAPrepHistory> getOaPrepHistory() {
        return oaPrepHistory;
    }

    public void setOaPrepHistory(List<OAPrepHistory> oaPrepHistory) {
        this.oaPrepHistory = oaPrepHistory;
    }
}
