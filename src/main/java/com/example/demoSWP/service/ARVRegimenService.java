package com.example.demoSWP.service;

import com.example.demoSWP.dto.ARVRegimenDTO;
import com.example.demoSWP.entity.ARVRegimen;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.repository.ARVRegimenRepository;
import com.example.demoSWP.repository.CustomerRepository;
import com.example.demoSWP.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ARVRegimenService {

    @Autowired
    private ARVRegimenRepository arvRegimenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public List<ARVRegimenDTO> getAllRegimens() {
        return arvRegimenRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ARVRegimenDTO getById(Long id) {
        return arvRegimenRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public ARVRegimenDTO createOrUpdate(ARVRegimenDTO dto) {
        ARVRegimen regimen = new ARVRegimen();

        if (dto.getArvRegimenId() != null) {
            regimen = arvRegimenRepository.findById(dto.getArvRegimenId()).orElse(new ARVRegimen());
        }

        // Gán thông tin cơ bản
        regimen.setRegimenName(dto.getRegimenName());
        regimen.setRegimenCode(dto.getRegimenCode());
        regimen.setDescription(dto.getDescription());
        regimen.setCreateDate(dto.getCreateDate());
        regimen.setDuration(dto.getDuration());
        regimen.setMedicationSchedule(dto.getMedicationSchedule());

        // Tính ngày kết thúc
        if (dto.getCreateDate() != null && dto.getDuration() > 0) {
            regimen.setEndDate(dto.getCreateDate().plusMonths(dto.getDuration()));
        }

        // Gán bác sĩ
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));
        regimen.setDoctor(doctor);

        // Gán bệnh nhân
        Customer customer = null;

        if (dto.getCustomerId() != null) {
            customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân với ID " + dto.getCustomerId()));
        } else if (dto.getCustomerName() != null && !dto.getCustomerName().isBlank()) {
            customer = customerRepository.findByFullName(dto.getCustomerName())
                    .orElseGet(() -> {
                        Customer newCus = new Customer();
                        newCus.setFullName(dto.getCustomerName());
                        return customerRepository.save(newCus);
                    });
        } else {
            throw new RuntimeException("Thiếu thông tin bệnh nhân");
        }

        regimen.setCustomer(customer);

        ARVRegimen saved = arvRegimenRepository.save(regimen);
        return convertToDTO(saved);
    }

    public void delete(Long id) {
        arvRegimenRepository.deleteById(id);
    }

    private ARVRegimenDTO convertToDTO(ARVRegimen entity) {
        ARVRegimenDTO dto = new ARVRegimenDTO();
        dto.setArvRegimenId(entity.getArvRegimenId());
        dto.setRegimenName(entity.getRegimenName());
        dto.setRegimenCode(entity.getRegimenCode());
        dto.setDescription(entity.getDescription());
        dto.setCreateDate(entity.getCreateDate());
        dto.setDuration(entity.getDuration());
        dto.setEndDate(entity.getEndDate());
        dto.setMedicationSchedule(entity.getMedicationSchedule());

        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getCustomerID());
            dto.setCustomerName(entity.getCustomer().getFullName());
        }

        if (entity.getDoctor() != null) {
            dto.setDoctorId(entity.getDoctor().getDoctorId());
            dto.setDoctorName(entity.getDoctor().getFullName());
        }

        return dto;
    }
}
