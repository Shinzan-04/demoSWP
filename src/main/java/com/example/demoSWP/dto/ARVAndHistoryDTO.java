package com.example.demoSWP.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ARVAndHistoryDTO {
    // ID của ARV Regimen (dùng cho update)
    private Long arvRegimenId;

    // ARV Regimen
    private Long doctorId;
    private Long customerId;
    private String customerName;
    private String email;
    private String regimenName;
    private String regimenCode;
    private Date createDate;
    private Date endDate;
    private String description;
    private String medicationSchedule;
    private int duration;

    // Medical History
    private String diseaseName;
    private String diagnosis;
    private String prescription;
    private String reason;
    private String treatment;
    private String notes;
}