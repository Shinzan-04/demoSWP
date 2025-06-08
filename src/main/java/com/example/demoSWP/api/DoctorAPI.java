// src/main/java/com/example/demoSWP/api/DoctorAPI.java
package com.example.demoSWP.api;

import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // Indicates that this class is a REST controller
@RequestMapping("/api/doctors") // Maps all requests to /api/doctors
@CrossOrigin(origins = "http://localhost:3000") // Allows cross-origin requests from http://localhost:3000
public class DoctorAPI {

    private final DoctorService doctorService;

    // Constructor injection for DoctorService
    @Autowired
    public DoctorAPI(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    /**
     * Handles GET requests to retrieve all doctors.
     * @return ResponseEntity with a list of all Doctors and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    // You can add other API methods (POST, PUT, DELETE) for doctors here if needed
}
