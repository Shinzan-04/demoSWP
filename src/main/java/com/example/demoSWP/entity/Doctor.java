package com.example.demoSWP.entity;

import com.example.demoSWP.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId; // ✅ đặt lại tên rõ ràng

    private String fullName;

    private String specialization; // ✅ thêm chuyên khoa
    private String avatarUrl;
    private String phone;
    private String description;

    @Email(message = "Email bác sĩ không hợp lệ")
    @Column(unique = true, nullable = false)
    private String email;

    private int workExperienceYears; // ✅ số năm kinh nghiệm

    @Enumerated(EnumType.STRING)
    private Role role; // ✅ phân quyền bác sĩ nếu cần

    @JsonManagedReference
    @OneToOne
    @JoinColumn(name = "account_id", unique = true)
    private Account account;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    private List<BlogPost> posts;

    public Doctor() {}

    public Doctor(String fullName, String specialization, String phone, String email, int workExperienceYears, Role role, Account account) {
        this.fullName = fullName;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.workExperienceYears = workExperienceYears;
        this.account = account;
    }
    @OneToMany(mappedBy = "doctor")
    @JsonIgnore
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "doctor")
    @JsonIgnore
    private List<TestResult> testResults;

}
