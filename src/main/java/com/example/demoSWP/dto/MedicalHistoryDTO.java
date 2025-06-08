package com.example.demoSWP.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MedicalHistoryDTO {
    private Long medicalHistoryId;
    private Long customerId;
    private String diseaseName;
    private LocalDate diagnosisDate;
    private String note;
}
