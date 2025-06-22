package com.example.demoSWP.service;

import com.example.demoSWP.dto.ARVAndHistoryDTO;
import com.example.demoSWP.dto.ARVRegimenDTO;
import com.example.demoSWP.entity.*;
import com.example.demoSWP.enums.Gender;
import com.example.demoSWP.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
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

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

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
    public List<ARVRegimenDTO> getByCustomerId(Long customerId) {
        return arvRegimenRepository.findByCustomerCustomerID(customerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ARVRegimenDTO createOrUpdate(ARVRegimenDTO dto) {
        ARVRegimen regimen = new ARVRegimen();

        if (dto.getArvRegimenId() != null) {
            regimen = arvRegimenRepository.findById(dto.getArvRegimenId()).orElse(new ARVRegimen());
        }

        regimen.setRegimenName(dto.getRegimenName());
        regimen.setRegimenCode(dto.getRegimenCode());
        regimen.setDescription(dto.getDescription());
        regimen.setCreateDate(dto.getCreateDate());
        regimen.setDuration(dto.getDuration());
        regimen.setMedicationSchedule(dto.getMedicationSchedule());

        if (dto.getCreateDate() != null && dto.getDuration() > 0) {
            regimen.setEndDate(dto.getCreateDate().plusMonths(dto.getDuration()));
        }

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));
        regimen.setDoctor(doctor);

        Customer customer = null;
        if (dto.getCustomerId() != null) {
            customer = customerRepository.findById(dto.getCustomerId()).orElse(null);
        } else if (dto.getEmail() != null) {
            customer = customerRepository.findByEmail(dto.getEmail()).orElse(null);
            if (customer == null) {
                customer = new Customer();
                customer.setEmail(dto.getEmail());
                customer.setFullName(dto.getCustomerName());
                customerRepository.save(customer);
            }
        }

        regimen.setCustomer(customer);

        ARVRegimen saved = arvRegimenRepository.save(regimen);
        return convertToDTO(saved);
    }

    public void delete(Long id) {
        Optional<ARVRegimen> optional = arvRegimenRepository.findById(id);
        if (optional.isPresent()) {
            ARVRegimen regimen = optional.get();
            List<MedicalHistory> histories = medicalHistoryRepository.findByArvRegimen_ArvRegimenId(regimen.getArvRegimenId());
            medicalHistoryRepository.deleteAll(histories);
        }
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

        // ✅ Thêm đầy đủ thông tin customer
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getCustomerID());
            dto.setCustomerName(entity.getCustomer().getFullName()); // ✅ dòng này là quan trọng
            dto.setEmail(entity.getCustomer().getEmail());
        }

        if (entity.getDoctor() != null) {
            dto.setDoctorId(entity.getDoctor().getDoctorId());
            dto.setDoctorName(entity.getDoctor().getFullName());
        }

        List<MedicalHistory> histories = medicalHistoryRepository.findByArvRegimen_ArvRegimenId(entity.getArvRegimenId());
        if (!histories.isEmpty()) {
            MedicalHistory history = histories.get(0);
            dto.setDiseaseName(history.getDiseaseName());
            dto.setDiagnosis(history.getDiagnosis());
            dto.setPrescription(history.getPrescription());
            dto.setReason(history.getReason());
            dto.setTreatment(history.getTreatment());
            dto.setNotes(history.getNotes());
        }

        return dto;
    }



    @Transactional
    public void saveARVWithMedicalHistory(ARVAndHistoryDTO dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));

        Customer customer = null;
        if (dto.getCustomerId() != null) {
            customer = customerRepository.findById(dto.getCustomerId()).orElse(null);
        } else if (dto.getEmail() != null) {
            customer = customerRepository.findByEmail(dto.getEmail()).orElse(null);
            if (customer == null) {
                customer = new Customer();
                customer.setEmail(dto.getEmail());
                customer.setFullName(dto.getCustomerName());
                customerRepository.save(customer);
            }
        }

        LocalDate createDate = dto.getCreateDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        ARVRegimen arv = new ARVRegimen();
        arv.setDoctor(doctor);
        arv.setCustomer(customer);
        arv.setRegimenName(dto.getRegimenName());
        arv.setRegimenCode(dto.getRegimenCode());
        arv.setCreateDate(createDate);
        arv.setDuration(dto.getDuration());
        arv.setDescription(dto.getDescription());
        arv.setMedicationSchedule(dto.getMedicationSchedule());

        if (dto.getDuration() > 0) {
            arv.setEndDate(createDate.plusMonths(dto.getDuration()));
        }

        ARVRegimen savedArv = arvRegimenRepository.save(arv);

        MedicalHistory history = new MedicalHistory();
        history.setDoctor(doctor);
        history.setCustomer(customer);
        history.setArvRegimen(savedArv);
        history.setVisitDate(createDate);
        history.setDiseaseName(dto.getDiseaseName());
        history.setDiagnosis(dto.getDiagnosis());
        history.setPrescription(dto.getPrescription());
        history.setReason(dto.getReason());
        history.setTreatment(dto.getTreatment());
        history.setNotes(dto.getNotes());

        medicalHistoryRepository.save(history);
    }


    @Transactional
    public void updateARVWithMedicalHistory(ARVAndHistoryDTO dto) {
        ARVRegimen arv = arvRegimenRepository.findById(dto.getArvRegimenId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ARV Regimen"));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));

        Customer customer = null;
        if (dto.getCustomerId() != null) {
            customer = customerRepository.findById(dto.getCustomerId()).orElse(null);
        } else if (dto.getEmail() != null) {
            customer = customerRepository.findByEmail(dto.getEmail()).orElse(null);
            if (customer == null) {
                customer = new Customer();
                customer.setEmail(dto.getEmail());
                customer.setFullName(dto.getCustomerName());
                customerRepository.save(customer);
            }
        }

        LocalDate createDate = dto.getCreateDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        arv.setDoctor(doctor);
        arv.setCustomer(customer);
        arv.setRegimenName(dto.getRegimenName());
        arv.setRegimenCode(dto.getRegimenCode());
        arv.setCreateDate(createDate);
        arv.setDuration(dto.getDuration());
        arv.setDescription(dto.getDescription());
        arv.setMedicationSchedule(dto.getMedicationSchedule());

        if (dto.getDuration() > 0) {
            arv.setEndDate(createDate.plusMonths(dto.getDuration()));
        }

        ARVRegimen savedArv = arvRegimenRepository.save(arv);

        List<MedicalHistory> histories = medicalHistoryRepository.findByArvRegimen_ArvRegimenId(savedArv.getArvRegimenId());
        MedicalHistory history;
        if (!histories.isEmpty()) {
            history = histories.get(0);
        } else {
            history = new MedicalHistory();
            history.setArvRegimen(savedArv);
        }

        history.setDoctor(doctor);
        history.setCustomer(customer);
        history.setVisitDate(createDate);
        history.setDiseaseName(dto.getDiseaseName());
        history.setDiagnosis(dto.getDiagnosis());
        history.setPrescription(dto.getPrescription());
        history.setReason(dto.getReason());
        history.setTreatment(dto.getTreatment());
        history.setNotes(dto.getNotes());

        medicalHistoryRepository.save(history);
    }

}
