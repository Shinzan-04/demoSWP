package com.example.demoSWP.dto;

import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private Long doctorId;
    private String fullName;
    private String specialization;
    private String phone;
    private String email;
    private int workExperienceYears;
    private String description;
    private String avatarUrl;
    private Role role;
    public DoctorDTO(String fullName, String phone, String email) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }
    public static DoctorDTO fromEntity(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setDoctorId(doctor.getDoctorId());
        dto.setFullName(doctor.getFullName());
        dto.setPhone(doctor.getPhone());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setEmail(doctor.getEmail());
        dto.setWorkExperienceYears(doctor.getWorkExperienceYears());
        dto.setDescription(doctor.getDescription()); // THÊM
        dto.setAvatarUrl(doctor.getAvatarUrl());     // THÊM
        return dto;
    }



}
