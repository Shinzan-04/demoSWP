package com.example.demoSWP.dto;

import com.example.demoSWP.enums.VisitType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class RegistrationResponse {
    private Long registrationId;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private String symptom;
    private String mode;
    private LocalDate dateOfBirth;
    private String address;
    private String notes;
    private String gender;
    private String doctorName;
    private Long doctorId;
    private Long slotId;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate appointmentDate;
    private VisitType visitType;
    private boolean status;
}
