package com.example.demoSWP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class MedicalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicalHistoryId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String diseaseName;
    private LocalDate diagnosisDate;
    private String note;
}
