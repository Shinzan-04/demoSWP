package com.example.demoSWP.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;


    private LocalTime startTime;
    private LocalTime endTime;

    private boolean isAvailable; // hoặc kiểm tra registration tồn tại
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    @JsonIgnore
    private Schedule schedule;

    @OneToMany(mappedBy = "slot")
    private List<Registration> registrations;
}
