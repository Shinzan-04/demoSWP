// src/main/java/com/example/demoSWP/api/RegistrationAPI.java
package com.example.demoSWP.api;

import com.example.demoSWP.entity.Registration;
import com.example.demoSWP.payload.request.RegistrationRequest; // Make sure this is imported
import com.example.demoSWP.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "http://localhost:3000")
public class RegistrationAPI {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationAPI(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<Registration> createRegistration(@RequestBody RegistrationRequest request) { // Changed parameter type
        Registration createdRegistration = registrationService.saveRegistrationFromRequest(request); // Calling the correct method
        return new ResponseEntity<>(createdRegistration, HttpStatus.CREATED);
    }

    // ... other API methods if any
}
    