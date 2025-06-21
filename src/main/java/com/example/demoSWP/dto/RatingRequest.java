package com.example.demoSWP.dto;

import com.example.demoSWP.entity.Rating;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
    int star;
    long doctorId;
    String comment;


}
