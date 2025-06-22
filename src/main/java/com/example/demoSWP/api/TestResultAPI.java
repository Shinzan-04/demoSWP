package com.example.demoSWP.api;

import com.example.demoSWP.dto.TestResultDTO;
import com.example.demoSWP.service.TestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-results")
@CrossOrigin(origins = "http://localhost:3000")
public class TestResultAPI {

    @Autowired
    private TestResultService testResultService;

    @GetMapping
    public List<TestResultDTO> getAll() {
        return testResultService.getAll();
    }

    @GetMapping("/doctor/{doctorId}")
    public List<TestResultDTO> getByDoctorId(@PathVariable Long doctorId) {
        return testResultService.getByDoctorId(doctorId);
    }

    @GetMapping("/{id}")
    public TestResultDTO getById(@PathVariable Long id) {
        return testResultService.getById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kết quả với id: " + id));
    }
    @GetMapping("/me")
    public List<TestResultDTO> getMyTestResults() {
        return testResultService.getByCurrentCustomer();
    }


    @PostMapping
    public TestResultDTO create(@RequestBody TestResultDTO dto) {
        return testResultService.create(dto);
    }

    @PutMapping("/{id}")
    public TestResultDTO update(@PathVariable Long id, @RequestBody TestResultDTO dto) {
        return testResultService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        testResultService.delete(id);
    }
}