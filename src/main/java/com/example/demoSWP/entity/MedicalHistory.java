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
    @JoinColumn(name = "customer_id", nullable = true) // Liên kết khóa ngoại với bảng Customer
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "arv_regimen_id")
    private ARVRegimen arvRegimen;
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    @Column(name = "disease_name")
    private String diseaseName;

    private LocalDate visitDate; // Date of the visit

    private String reason; // Reason for the visit
    private String diagnosis; // Diagnosis made by the doctor
    private String treatment; // Treatment prescribed
    private String prescription; // Prescription details
    private String notes; // Additional notes
}
