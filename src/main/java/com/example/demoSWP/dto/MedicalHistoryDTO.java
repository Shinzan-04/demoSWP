package com.example.demoSWP.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MedicalHistoryDTO {
    private Long medicalHistoryId; // Unique ID for the medical history record
    private Long customerID; // ID of the customer
    private LocalDate visitDate; // Date of the visit
    private Long doctorId; // ID of the doctor
    private String reason; // Reason for the visit
    private String diagnosis; // Diagnosis made by the doctor
    private String treatment; // Treatment prescribed
    private String prescription; // Prescription details
    private String notes; // Additional notes
}
