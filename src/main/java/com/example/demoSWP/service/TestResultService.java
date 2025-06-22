package com.example.demoSWP.service;

import com.example.demoSWP.dto.TestResultDTO;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.TestResult;
import com.example.demoSWP.repository.CustomerRepository;
import com.example.demoSWP.repository.DoctorRepository;
import com.example.demoSWP.repository.TestResultRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    @Transactional
    public TestResultDTO create(TestResultDTO dto) {
        Customer customer = customerRepository.findByEmail(dto.getCustomerEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân với email: " + dto.getCustomerEmail()));

        Doctor doctor = getCurrentDoctor();

        TestResult testResult = new TestResult();
        testResult.setDate(dto.getDate());
        testResult.setTypeOfTest(dto.getTypeOfTest());
        testResult.setResultDescription(dto.getResultDescription());
        testResult.setCustomer(customer);
        testResult.setDoctor(doctor);

        TestResult saved = testResultRepository.save(testResult);
        return toDTO(saved);
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
        dto.setDoctorName(entity.getDoctor().getFullName()); // 👈 Thêm dòng này
        dto.setCustomerName(entity.getCustomer().getFullName());
        dto.setCustomerEmail(entity.getCustomer().getEmail());
        return dto;
    }

    private TestResult toEntity(TestResultDTO dto) {
        TestResult entity = new TestResult(); // KHÔNG map toàn bộ DTO
        entity.setDate(dto.getDate());
        entity.setTypeOfTest(dto.getTypeOfTest());
        entity.setResultDescription(dto.getResultDescription());

        entity.setCustomer(customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy customer")));

        entity.setDoctor(doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy doctor")));

        return entity;
    }
    public List<TestResultDTO> getByDoctorId(Long doctorId) {
        return testResultRepository.findByDoctorDoctorId(doctorId)
                .stream()
                .map(this::toDTO)
                .toList();
    }
    // Helper: lấy bác sĩ hiện tại từ context
    private Doctor getCurrentDoctor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return doctorRepository.findByAccount_Email(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với email đăng nhập: " + email));
    }
    public List<TestResultDTO> getByCurrentCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân với email: " + email));
        return testResultRepository.findByCustomerCustomerID(customer.getCustomerID())
                .stream()
                .map(this::toDTO)
                .toList();
    }


}