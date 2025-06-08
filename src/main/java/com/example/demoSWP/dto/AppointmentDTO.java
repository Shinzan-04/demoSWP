package com.example.demoSWP.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentDTO {
    private Long appointmentId;
    private Long customerId;
    private Long scheduleId;
    private String status;
    private String note;
}
