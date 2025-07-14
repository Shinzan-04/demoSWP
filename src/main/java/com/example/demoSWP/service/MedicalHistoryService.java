package com.example.demoSWP.service;

import com.example.demoSWP.dto.MedicalHistoryDTO;
import com.example.demoSWP.entity.Account;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.MedicalHistory;
import com.example.demoSWP.repository.CustomerRepository;
import com.example.demoSWP.repository.DoctorRepository;
import com.example.demoSWP.repository.MedicalHistoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public List<MedicalHistoryDTO> getAll() {
        return historyRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public Optional<MedicalHistoryDTO> getById(Long id) {
        return historyRepository.findById(id)
                .map(this::toDTO);
    }

    public List<MedicalHistoryDTO> getByCustomerId(Long customerId) {
        return historyRepository.findByCustomerCustomerID(customerId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ✅ Lấy lịch sử của bệnh nhân đang đăng nhập
    public List<MedicalHistoryDTO> getMyMedicalHistories() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Account account && account.getCustomer() != null) {
            Long customerId = account.getCustomer().getCustomerID();
            return historyRepository.findByCustomerCustomerID(customerId)
                    .stream()
                    .map(this::toDTO)
                    .toList();
        }

        throw new RuntimeException("Người dùng không hợp lệ hoặc không phải bệnh nhân.");
    }

    public MedicalHistoryDTO create(MedicalHistoryDTO dto) {
        MedicalHistory entity = new MedicalHistory();

        entity.setVisitDate(dto.getVisitDate());
        entity.setDiseaseName(dto.getDiseaseName());
        entity.setReason(dto.getReason());
        entity.setDiagnosis(dto.getDiagnosis());
        entity.setTreatment(dto.getTreatment());
        entity.setPrescription(dto.getPrescription());
        entity.setNotes(dto.getNotes());

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));
        entity.setDoctor(doctor);

        if (dto.getCustomerID() != null) {
            customerRepository.findById(dto.getCustomerID()).ifPresent(entity::setCustomer);
        }

        MedicalHistory saved = historyRepository.save(entity);
        return toDTO(saved);
    }

    public MedicalHistoryDTO update(Long id, MedicalHistoryDTO dto) {
        return historyRepository.findById(id).map(existing -> {
            existing.setVisitDate(dto.getVisitDate());
            existing.setDiseaseName(dto.getDiseaseName());
            existing.setReason(dto.getReason());
            existing.setDiagnosis(dto.getDiagnosis());
            existing.setTreatment(dto.getTreatment());
            existing.setPrescription(dto.getPrescription());
            existing.setNotes(dto.getNotes());

            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));
            existing.setDoctor(doctor);

            if (dto.getCustomerID() != null) {
                customerRepository.findById(dto.getCustomerID()).ifPresent(existing::setCustomer);
            } else {
                existing.setCustomer(null);
            }

            return toDTO(historyRepository.save(existing));
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử với id: " + id));
    }

    public void delete(Long id) {
        historyRepository.deleteById(id);
    }

    private MedicalHistoryDTO toDTO(MedicalHistory entity) {
        MedicalHistoryDTO dto = modelMapper.map(entity, MedicalHistoryDTO.class);

        if (entity.getCustomer() != null) {
            dto.setCustomerID(entity.getCustomer().getCustomerID());
            dto.setCustomerName(entity.getCustomer().getFullName());
        }

        if (entity.getDoctor() != null) {
            dto.setDoctorId(entity.getDoctor().getDoctorId());
            dto.setDoctorName(entity.getDoctor().getFullName());
        }

        return dto;
    }
}