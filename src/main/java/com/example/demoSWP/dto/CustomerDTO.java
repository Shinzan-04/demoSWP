package com.example.demoSWP.dto;

import com.example.demoSWP.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {
    private Long customerID;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private Gender gender;
    private LocalDate dateOfBirth;
}
