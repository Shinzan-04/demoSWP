package com.example.demoSWP.entity;

import com.example.demoSWP.enums.ReminderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;


    @ManyToOne
    @JoinColumn(name = "arv_regimen_id")
    private ARVRegimen arvRegimen;

    // Thời điểm nhắc nhở
    private LocalDateTime reminderDate;

    private String reminderContent;

    @Enumerated(EnumType.STRING)
    private ReminderStatus status;
}
