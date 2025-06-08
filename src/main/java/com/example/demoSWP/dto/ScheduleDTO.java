package com.example.demoSWP.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ScheduleDTO {
    private Long scheduleId;
    private Long doctorId;        // 👈 FE chỉ gửi doctorId
    private LocalDate date;
    private LocalTime startTime;
}
