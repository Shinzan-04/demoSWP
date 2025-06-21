package com.example.demoSWP.entity;

import com.example.demoSWP.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers") // Tên bảng trong CSDL
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerID;



    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Lưu trữ enum dưới dạng chuỗi
    private Gender gender;

    private LocalDate dateOfBirth;


    @Column(unique = true) // Đảm bảo số điện thoại là duy nhất
    private String phone;
    private String avatarUrl;
    private String email;
    private String address;
    private String fullName;


    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    List<Rating> ratings;

}
