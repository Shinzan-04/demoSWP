package com.example.demoSWP.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class ScheduleDTO {
    private Long scheduleId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String room;
    private String patientName;
    private Long doctorId;
}
