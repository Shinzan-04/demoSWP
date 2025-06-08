package com.example.demoSWP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;
    private String email;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    // Thêm các trường khác của Staff nếu có (ví dụ: position, department, v.v.)
}