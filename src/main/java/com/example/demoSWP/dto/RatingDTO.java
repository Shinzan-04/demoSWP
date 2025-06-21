package com.example.demoSWP.dto;

import com.example.demoSWP.entity.Rating;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    private Long ratingId;
    private int rating;
    private String comment;
    private LocalDateTime createAt;
    private String customerName; // hoặc customerId nếu cần

    public static RatingDTO fromEntity(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setRatingId(rating.getRatingId());
        dto.setRating(rating.getRating());
        dto.setComment(rating.getComment());
        dto.setCreateAt(rating.getCreateAt());
        dto.setCustomerName(
                rating.getCustomer() != null ? rating.getCustomer().getFullName() : null
        );
        return dto;
    }
}
