package com.example.demoSWP.service;

import com.example.demoSWP.dto.MedicalHistoryDTO;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.entity.MedicalHistory;
import com.example.demoSWP.repository.CustomerRepository;
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

    public MedicalHistoryDTO create(MedicalHistoryDTO dto) {
        MedicalHistory entity = modelMapper.map(dto, MedicalHistory.class);

        Customer customer = customerRepository.findById(dto.getCustomerID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy customer"));
        entity.setCustomer(customer);

        MedicalHistory saved = historyRepository.save(entity);
        return toDTO(saved);
    }
    public List<MedicalHistoryDTO> getByCustomerId(Long customerId) {
        return historyRepository.findByCustomerCustomerID(customerId).stream()
                .map(this::toDTO)
                .toList();
    }




    public MedicalHistoryDTO update(Long id, MedicalHistoryDTO dto) {
        return historyRepository.findById(id).map(existing -> {
            existing.setVisitDate(dto.getVisitDate());
            existing.setDoctorId(dto.getDoctorId());
            existing.setReason(dto.getReason());
            existing.setDiagnosis(dto.getDiagnosis());
            existing.setTreatment(dto.getTreatment());
            existing.setPrescription(dto.getPrescription());
            existing.setNotes(dto.getNotes());

            Customer customer = customerRepository.findById(dto.getCustomerID())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy customer"));
            existing.setCustomer(customer);

            MedicalHistory updated = historyRepository.save(existing);
            return toDTO(updated);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử với id: " + id));
    }

    public void delete(Long id) {
        historyRepository.deleteById(id);
    }

    private MedicalHistoryDTO toDTO(MedicalHistory entity) {
        MedicalHistoryDTO dto = modelMapper.map(entity, MedicalHistoryDTO.class);
        if (entity.getCustomer() != null) {
            dto.setCustomerID(entity.getCustomer().getCustomerID());
        } else {
            dto.setCustomerID(null);
        }
        return dto;
    }

    private MedicalHistory toEntity(MedicalHistoryDTO dto) {
        MedicalHistory entity = modelMapper.map(dto, MedicalHistory.class);
        Customer customer = customerRepository.findById(dto.getCustomerID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy customer"));
        entity.setCustomer(customer);
        return entity;
    }
}

