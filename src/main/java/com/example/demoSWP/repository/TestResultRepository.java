package com.example.demoSWP.repository;

import com.example.demoSWP.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByDoctorDoctorId(Long doctorId);
    List<TestResult> findByCustomerCustomerID(Long customerId);


}