package com.example.demoSWP.service;

import com.example.demoSWP.dto.TestResultDTO;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.TestResult;
import com.example.demoSWP.repository.CustomerRepository;
import com.example.demoSWP.repository.DoctorRepository;
import com.example.demoSWP.repository.TestResultRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestResultService {

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<TestResultDTO> getAll() {
        return testResultRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public Optional<TestResultDTO> getById(Long id) {
        return testResultRepository.findById(id)
                .map(this::toDTO);
    }

    public TestResultDTO create(TestResultDTO dto) {
        TestResult entity = toEntity(dto);
        return toDTO(testResultRepository.save(entity));
    }

    public TestResultDTO update(Long id, TestResultDTO dto) {
        return testResultRepository.findById(id)
                .map(existing -> {
                    existing.setDate(dto.getDate());
                    existing.setTypeOfTest(dto.getTypeOfTest());
                    existing.setResultDescription(dto.getResultDescription());

                    existing.setCustomer(customerRepository.findById(dto.getCustomerId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy customer")));

                    existing.setDoctor(doctorRepository.findById(dto.getDoctorId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy doctor")));

                    return toDTO(testResultRepository.save(existing));
                }).orElseThrow(() -> new RuntimeException("Không tìm thấy kết quả test"));
    }

    public void delete(Long id) {
        testResultRepository.deleteById(id);
    }

    private TestResultDTO toDTO(TestResult entity) {
        TestResultDTO dto = modelMapper.map(entity, TestResultDTO.class);
        dto.setCustomerId(entity.getCustomer().getCustomerID());
        dto.setDoctorId(entity.getDoctor().getDoctorId());
        return dto;
    }

    private TestResult toEntity(TestResultDTO dto) {
        TestResult entity = modelMapper.map(dto, TestResult.class);
        entity.setCustomer(customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy customer")));
        entity.setDoctor(doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy doctor")));
        return entity;
    }
}
