// src/main/java/com/example/demoSWP/dto/AppointmentRequest.java
package com.example.demoSWP.dto;

import com.example.demoSWP.enums.VisitType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {

    private Long registrationId;
    private Long doctorId;
    @NotBlank(message = "Email khách hàng không được để trống")
    @Email(message = "Email khách hàng không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Chuyên khoa không được để trống")
    private String specialization;

    @NotNull(message = "Ngày hẹn không được để trống")
    private Date appointmentDate;


    @NotBlank(message = "Online/Offline không được để trống")
    private String mode;

    private String notes;
    private String symptom; // Triệu chứng mô tả (có thể để trống)
    private final VisitType visitType = VisitType.APPOINTMENT;

    private Long slotId;
}
