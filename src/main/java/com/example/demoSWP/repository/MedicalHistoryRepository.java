package com.example.demoSWP.repository;

import com.example.demoSWP.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    List<MedicalHistory> findByCustomerCustomerID(Long customerId);

    List<MedicalHistory> findByArvRegimen_ArvRegimenId(Long arvRegimenId);

}
