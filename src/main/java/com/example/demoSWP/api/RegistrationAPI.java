// src/main/java/com/example/demoSWP/api/RegistrationAPI.java
package com.example.demoSWP.api;

import com.example.demoSWP.dto.RegistrationRequest;
import com.example.demoSWP.dto.RegistrationResponse;
import com.example.demoSWP.entity.Registration;
import com.example.demoSWP.exception.RegistrationNotFoundException;
import com.example.demoSWP.service.RegistrationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/registrations")
@SecurityRequirement(name = "api")
@CrossOrigin(origins = "http://localhost:3000")
public class RegistrationAPI {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationAPI(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<RegistrationResponse> createRegistration(@RequestBody RegistrationRequest request) {
        Registration created = registrationService.saveRegistrationFromRequest(request);
        RegistrationResponse response = registrationService.convertToDTO(created);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    // ✅ Lấy tất cả đăng ký (dạng DTO)
    @GetMapping
    public ResponseEntity<List<RegistrationResponse>> getAllRegistrations() {
        List<RegistrationResponse> dtos = registrationService.getAllRegistrations();
        return ResponseEntity.ok(dtos);
    }
    // ✅ Lấy tất cả đăng ký có status = true
    @GetMapping("/active")
    public ResponseEntity<List<RegistrationResponse>> getAllRegistrationsWithStatus() {
        List<RegistrationResponse> dtos = registrationService.getAllRegistrationsWithStatus();
        return ResponseEntity.ok(dtos);
    }

    // ✅ Lấy một đăng ký theo ID
    @GetMapping("/{id}")
    public ResponseEntity<RegistrationResponse> getRegistrationById(@PathVariable Long id) {
        return registrationService.getRegistrationById(id)
                .map(reg -> ResponseEntity.ok(registrationService.convertToDTO(reg)))
                .orElseThrow(() -> new RegistrationNotFoundException("Không tìm thấy đăng ký với ID: " + id));
    }

    // ✅ Xoá đăng ký
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable Long id) {
        registrationService.deleteRegistration(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/doctor-register")
    public ResponseEntity<RegistrationResponse> registerByDoctor(@RequestBody RegistrationRequest request) {
        Registration reg = registrationService.saveRegistrationFromRequest(request);
        RegistrationResponse response = registrationService.convertToDTO(reg);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    // ✅ Chỉnh status

    @PatchMapping("/{id}/status")
    public ResponseEntity<RegistrationResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam boolean status) {
        Registration updated = registrationService.updateStatus(id, status);
        RegistrationResponse response = registrationService.convertToDTO(updated);
        return ResponseEntity.ok(response);
    }


    // ... other API methods if any
}
    