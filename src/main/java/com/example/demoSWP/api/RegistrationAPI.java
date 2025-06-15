// src/main/java/com/example/demoSWP/api/RegistrationAPI.java
package com.example.demoSWP.api;

import com.example.demoSWP.dto.RegistrationRequest;
import com.example.demoSWP.entity.Registration;
import com.example.demoSWP.exception.RegistrationNotFoundException;
import com.example.demoSWP.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    // ✅ Lấy tất cả đăng ký (dạng DTO)
    @GetMapping
    public ResponseEntity<List<RegistrationRequest>> getAllRegistrations() {
        List<RegistrationRequest> dtos = registrationService.getAllRegistrations();
        return ResponseEntity.ok(dtos);
    }


    // ✅ Lấy một đăng ký theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Registration> getRegistrationById(@PathVariable Long id) {
        return registrationService.getRegistrationById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RegistrationNotFoundException("Không tìm thấy đăng ký với ID: " + id));
    }
    // ✅ Xoá đăng ký
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable Long id) {
        registrationService.deleteRegistration(id);
        return ResponseEntity.noContent().build();
    }
    // ... other API methods if any
}
    