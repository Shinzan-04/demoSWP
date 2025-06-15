package com.example.demoSWP.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ARVRegimenDTO {
    private Long arvRegimenId;
    private Long doctorId;
    private String doctorName;
    private Long customerId;
    private String customerName;
    private LocalDate createDate;
    private String regimenName;
    private String regimenCode;
    private String description;
    private int duration;
    private LocalDate endDate;
    private String medicationSchedule;
}
