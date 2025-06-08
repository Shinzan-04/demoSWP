package com.example.demoSWP.dto;

import com.example.demoSWP.enums.ReminderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime; // For reminderDate

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderRequest {

    @NotNull(message = "Customer ID không được để trống")
    private Long customerId; // ID của khách hàng

    @NotNull(message = "ARV Regimen ID không được để trống")
    private Long arvRegimenId; // ID của phác đồ ARV

    @NotNull(message = "Ngày nhắc nhở không được để trống")
    private LocalDateTime reminderDate; // Ngày và giờ nhắc nhở

    @NotBlank(message = "Nội dung nhắc nhở không được để trống")
    private String reminderContent; // Nội dung của nhắc nhở

    private ReminderStatus status;
    // Status sẽ được gán mặc định là PENDING ở backend
    // private ReminderStatus status;
}
