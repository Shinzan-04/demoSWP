package com.example.demoSWP.service;

import com.example.demoSWP.dto.MedicalHistoryDTO;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.MedicalHistory;
import com.example.demoSWP.repository.CustomerRepository;
import com.example.demoSWP.repository.DoctorRepository;
import com.example.demoSWP.repository.MedicalHistoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalHistoryService {

    @Autowired
    private MedicalHistoryRepository historyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Lấy toàn bộ lịch sử
    public List<MedicalHistoryDTO> getAll() {
        return historyRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Lấy theo ID
    public Optional<MedicalHistoryDTO> getById(Long id) {
        return historyRepository.findById(id)
                .map(this::toDTO);
    }

    // Lấy theo bệnh nhân
    public List<MedicalHistoryDTO> getByCustomerId(Long customerId) {
        return historyRepository.findByCustomerCustomerID(customerId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Tạo mới lịch sử khám
    public MedicalHistoryDTO create(MedicalHistoryDTO dto) {
        MedicalHistory entity = new MedicalHistory();

        entity.setVisitDate(dto.getVisitDate());
        entity.setDiseaseName(dto.getDiseaseName());
        entity.setReason(dto.getReason());
        entity.setDiagnosis(dto.getDiagnosis());
        entity.setTreatment(dto.getTreatment());
        entity.setPrescription(dto.getPrescription());
        entity.setNotes(dto.getNotes());

        // Set bác sĩ
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));
        entity.setDoctor(doctor);

        // Set bệnh nhân (cho phép null nếu không tồn tại)
        if (dto.getCustomerID() != null) {
            customerRepository.findById(dto.getCustomerID()).ifPresent(entity::setCustomer);
        }

        MedicalHistory saved = historyRepository.save(entity);
        return toDTO(saved);
    }

    // Cập nhật
    public MedicalHistoryDTO update(Long id, MedicalHistoryDTO dto) {
        return historyRepository.findById(id).map(existing -> {
            existing.setVisitDate(dto.getVisitDate());
            existing.setDiseaseName(dto.getDiseaseName());
            existing.setReason(dto.getReason());
            existing.setDiagnosis(dto.getDiagnosis());
            existing.setTreatment(dto.getTreatment());
            existing.setPrescription(dto.getPrescription());
            existing.setNotes(dto.getNotes());

            // Bác sĩ
            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));
            existing.setDoctor(doctor);

            // Bệnh nhân
            if (dto.getCustomerID() != null) {
                customerRepository.findById(dto.getCustomerID()).ifPresent(existing::setCustomer);
            } else {
                existing.setCustomer(null);
            }

            return toDTO(historyRepository.save(existing));
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử với id: " + id));
    }

    // Xoá
    public void delete(Long id) {
        historyRepository.deleteById(id);
    }

    // Mapping entity -> DTO
    private MedicalHistoryDTO toDTO(MedicalHistory entity) {
        MedicalHistoryDTO dto = modelMapper.map(entity, MedicalHistoryDTO.class);

        if (entity.getCustomer() != null) {
            dto.setCustomerID(entity.getCustomer().getCustomerID());
            dto.setCustomerName(entity.getCustomer().getFullName()); // <-- Thêm dòng này
        }

        if (entity.getDoctor() != null) {
            dto.setDoctorId(entity.getDoctor().getDoctorId());
            dto.setDoctorName(entity.getDoctor().getFullName());
        }

        return dto;
    }
}
