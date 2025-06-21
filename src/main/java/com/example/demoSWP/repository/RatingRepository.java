package com.example.demoSWP.repository;

import com.example.demoSWP.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByCustomerAndDoctor(Customer customer, Doctor doctor);

    long countByDoctor_DoctorId(Long doctorId);


}
