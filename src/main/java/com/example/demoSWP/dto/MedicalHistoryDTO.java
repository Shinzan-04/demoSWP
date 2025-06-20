package com.example.demoSWP.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MedicalHistoryDTO {
    private Long medicalHistoryId;       // ID của bản ghi lịch sử
    private Long customerID;             // ID bệnh nhân
    private LocalDate visitDate;         // Ngày khám
    private Long doctorId;               // ID bác sĩ
    private String doctorName;           // Tên bác sĩ (để hiển thị)
    private String diseaseName;          // Tên bệnh (BỊ THIẾU)
    private String reason;               // Lý do khám
    private String diagnosis;            // Chẩn đoán
    private String treatment;            // Phác đồ điều trị
    private String prescription;         // Đơn thuốc
    private String notes;                // Ghi chú thêm
}
