package com.example.demoSWP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class ARVRegimen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long arvRegimenId;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDate createDate;
    private String regimenName;
    private String regimenCode;
    private String description;

    // Thời gian điều trị theo tháng
    private int duration;

    // Ngày kết thúc điều trị (có thể tính từ createDate + duration)
    private LocalDate endDate;

    // Lịch uống thuốc, ví dụ: "Sáng, Trưa, Tối"
    private String medicationSchedule;
}
