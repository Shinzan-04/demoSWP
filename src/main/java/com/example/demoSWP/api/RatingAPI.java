package com.example.demoSWP.api;

import com.example.demoSWP.dto.RatingRequest;
import com.example.demoSWP.entity.Rating;
import com.example.demoSWP.service.RatingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rating")
@CrossOrigin(origins = "http://localhost:3000")
@SecurityRequirement(name = "api")
public class RatingAPI {

    @Autowired
    RatingService ratingService;

    @PostMapping
    public ResponseEntity createRating(@RequestBody RatingRequest ratingRequest) {
        Rating newRating = ratingService.create(ratingRequest);
        return ResponseEntity.ok(newRating);
    }
    @GetMapping("/count/{doctorId}")
    public ResponseEntity<Long> countRatings(@PathVariable Long doctorId) {
        long count = ratingService.countRatingsByDoctorId(doctorId);
        return ResponseEntity.ok(count);
    }

}
