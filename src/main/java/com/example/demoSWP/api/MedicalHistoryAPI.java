package com.example.demoSWP.api;

import com.example.demoSWP.dto.MedicalHistoryDTO;
import com.example.demoSWP.service.MedicalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-histories")
@CrossOrigin(origins = "http://localhost:3000")
public class MedicalHistoryAPI {

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    @GetMapping
    public List<MedicalHistoryDTO> getAll() {
        return medicalHistoryService.getAll();
    }

    @GetMapping("/customer/{customerId}")
    public List<MedicalHistoryDTO> getByCustomerId(@PathVariable Long customerId) {
        return medicalHistoryService.getByCustomerId(customerId);
    }



    @GetMapping("/{id}")
    public MedicalHistoryDTO getById(@PathVariable Long id) {
        return medicalHistoryService.getById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ bệnh với id " + id));
    }

    @PostMapping
    public MedicalHistoryDTO create(@RequestBody MedicalHistoryDTO dto) {
        return medicalHistoryService.create(dto);
    }

    @PutMapping("/{id}")
    public MedicalHistoryDTO update(@PathVariable Long id, @RequestBody MedicalHistoryDTO dto) {
        return medicalHistoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        medicalHistoryService.delete(id);
    }
}
