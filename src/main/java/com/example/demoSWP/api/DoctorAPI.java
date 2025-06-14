package com.example.demoSWP.api;

import com.example.demoSWP.dto.DoctorDTO;
import com.example.demoSWP.entity.Account;
import com.example.demoSWP.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorAPI {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public List<DoctorDTO> getAll() {
        return doctorService.getAll();
    }

    @GetMapping("/{id}")
    public DoctorDTO getById(@PathVariable Long id) {
        return doctorService.getById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID: " + id));
    }

    @PostMapping
    public DoctorDTO create(@RequestBody DoctorDTO dto) {
        return doctorService.create(dto);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DoctorDTO updateDoctorProfile(
            @PathVariable Long id,
            @RequestPart("doctor") DoctorDTO doctorDto,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        return doctorService.updateWithAvatar(id, doctorDto, avatarFile);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        doctorService.delete(id);
    }


    @GetMapping("/me")
    public DoctorDTO getMyProfile() {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (account == null || account.getDoctor() == null) {
            throw new RuntimeException("Không tìm thấy thông tin bác sĩ.");
        }

        return DoctorDTO.fromEntity(account.getDoctor()); // Chuyển đổi sang DTO nếu cần
    }

    @PutMapping(value = "/update-no-avatar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DoctorDTO updateDoctorNoAvatar(
            @PathVariable Long id,
            @RequestBody DoctorDTO doctorDto
    ) {
        return doctorService.update(id, doctorDto);
    }

    @GetMapping("/with-avatar")
    public ResponseEntity<List<DoctorDTO>> getDoctorsWithAvatar() {
        List<DoctorDTO> doctorsWithAvatar = doctorService.getAllDoctorsWithAvatar();
        return ResponseEntity.ok(doctorsWithAvatar);
    }


}
