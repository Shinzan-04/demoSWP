package com.example.demoSWP.dto;// src/main/java/com/example/demoSWP/payload/request/RegistrationRequest.java


import com.example.demoSWP.enums.Gender; // Assuming you have an enum for Gender
import com.example.demoSWP.enums.VisitType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate; // For dateOfBirth
import java.util.Date; // For appointmentDate

@Getter // Lombok annotation to generate getters for all fields
@Setter // Lombok annotation to generate setters for all fields
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor
@AllArgsConstructor // Lombok annotation to generate a constructor with all fields
public class RegistrationRequest {

    private Long registrationID;
    private Long doctorId;
    // Customer Information from the form
    private String fullName;

    @NotBlank(message = "Email khách hàng không được để trống")
    @Email(message = "Email khách hàng không hợp lệ")
    private String email; // Customer's email

    private Gender gender; // Using Gender enum, assuming it exists

    @PastOrPresent(message = "Năm sinh không thể ở tương lai")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private String address;

    // Registration Details from the form
    @NotBlank(message = "Chuyên khoa không được để trống")
    private String specialization;

    @NotBlank(message = "Hình thức khám không được để trống")
    private String mode; // Online hoặc Offline

    @NotNull(message = "Ngày khám không được để trống")
    private Date appointmentDate; // Ngày khám

    @NotBlank(message = "Buổi khám không được để trống")
    private String session; // Buổi khám: Sáng/Chiều/Tối

    private String symptom; // Triệu chứng mô tả (có thể để trống)

    private String notes; // Thêm ghi chú (có thể để trống)
    private VisitType visitType;
}
