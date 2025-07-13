package com.example.demoSWP.repository;

import com.example.demoSWP.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByCustomerAndDoctor(Customer customer, Doctor doctor);

    long countByDoctor_DoctorId(Long doctorId);

    List<Rating> findAllByDoctor(Doctor doctor);
    Optional<Rating> findByCustomerAndDoctor(Customer customer, Doctor doctor);


}
