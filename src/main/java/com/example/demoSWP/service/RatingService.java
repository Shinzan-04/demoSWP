package com.example.demoSWP.service;

import com.example.demoSWP.dto.RatingRequest;
import com.example.demoSWP.entity.Account;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.Rating;
import com.example.demoSWP.exception.exceptions.BadRequestException;
import com.example.demoSWP.repository.DoctorRepository;
import com.example.demoSWP.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RatingService {
    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    DoctorRepository doctorRepository;

    public Rating create(RatingRequest ratingRequest) {
        if (ratingRequest.getStar() < 1 || ratingRequest.getStar() > 5) {
            throw new BadRequestException("Rating should be between 1 and 5");
        }

        Account currentAccount = authenticationService.getCurrentAccount();

        // lấy customer từ account
        Customer customer = currentAccount.getCustomer();
        if (customer == null) {
            throw new BadRequestException("Current account is not linked to a customer");
        }

        Doctor doctor = doctorRepository.findById(ratingRequest.getDoctorId())
                .orElseThrow(() -> new BadRequestException("Doctor not found"));

        // kiểm tra trùng
        if (ratingRepository.existsByCustomerAndDoctor(customer, doctor)) {
            throw new BadRequestException("Rating already exists");
        }
        System.out.println("Received RatingRequest: " + ratingRequest);


        Rating rating = new Rating();
        rating.setCustomer(customer);
        rating.setDoctor(doctor);
        rating.setRating(ratingRequest.getStar());
        rating.setComment(ratingRequest.getComment());
        rating.setCreateAt(LocalDateTime.now());

        return ratingRepository.save(rating);
    }

    public long countRatingsByDoctorId(Long doctorId) {
        return ratingRepository.countByDoctor_DoctorId(doctorId);
    }

}
