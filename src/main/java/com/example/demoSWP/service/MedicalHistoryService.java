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
        MedicalHistory entity = toEntity(dto);
        return toDTO(historyRepository.save(entity));
    }

    public MedicalHistoryDTO update(Long id, MedicalHistoryDTO dto) {
        return historyRepository.findById(id)
                .map(existing -> {
                    existing.setDiseaseName(dto.getDiseaseName());
                    existing.setDiagnosisDate(dto.getDiagnosisDate());
                    existing.setNote(dto.getNote());
                    Customer customer = customerRepository.findById(dto.getCustomerId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy customer"));
                    existing.setCustomer(customer);
                    return toDTO(historyRepository.save(existing));
                }).orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử với id: " + id));
    }

    public void delete(Long id) {
        historyRepository.deleteById(id);
    }

    private MedicalHistoryDTO toDTO(MedicalHistory entity) {
        MedicalHistoryDTO dto = modelMapper.map(entity, MedicalHistoryDTO.class);
        dto.setCustomerId(entity.getCustomer().getCustomerID());
        return dto;
    }

    private MedicalHistory toEntity(MedicalHistoryDTO dto) {
        MedicalHistory entity = modelMapper.map(dto, MedicalHistory.class);
        entity.setCustomer(customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy customer")));
        return entity;
    }
}
