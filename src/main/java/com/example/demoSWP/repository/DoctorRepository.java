package com.example.demoSWP.repository;

import com.example.demoSWP.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByAccount_Email(String email);
    Optional<Doctor> findByFullName(String fullName);
}

