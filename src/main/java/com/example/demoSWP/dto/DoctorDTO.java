package com.example.demoSWP.dto;

import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.Rating;
import com.example.demoSWP.enums.Role;
import lombok.*;

import java.util.List;

@Data
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

    private double averageRating;
    List<RatingDTO> ratings;





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
//        if (doctor.getRatings() != null && !doctor.getRatings().isEmpty()) {
//            double avg = doctor.getRatings().stream()
//                    .mapToInt(Rating::getRating)
//                    .average()
//                    .orElse(0.0);
//            dto.setAverageRating(Math.round(avg * 10.0) / 10.0);
//        } else {
//            dto.setAverageRating(0.0);
//        }
      //  dto.setRatings(doctor.getRatings());


        return dto;
    }



}
