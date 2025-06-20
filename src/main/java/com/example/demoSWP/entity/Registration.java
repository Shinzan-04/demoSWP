package com.example.demoSWP.entity;

import com.example.demoSWP.enums.Gender;
import com.example.demoSWP.enums.VisitType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Date; // Using java.util.Date as per previous Registration.java, but LocalDate is often preferred for dates without time


@Entity // Marks this class as a JPA entity
@Table(name = "registrations") // Specifies the table name in the database
@Getter // Lombok annotation to generate getters for all fields
@Setter // Lombok annotation to generate setters for all fields
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor
@AllArgsConstructor // Lombok annotation to generate a constructor with all fields
public class Registration {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures primary key generation strategy
    @Column(name = "registration_id") // Maps the field to a column named 'registration_id'
    private Long registrationID;


    private String fullName; // Liên kết với thực thể Customer
    private String email;
    private String phone;
    private String address;
    private Gender gender;

    private LocalDate dateOfBirth;

    @ManyToOne // Defines a many-to-one relationship with the Doctor entity
    @JoinColumn(name = "doctor_id", nullable = false) // Specifies the foreign key column in the 'registrations' table
    @JsonIgnore
    private Doctor doctor; // Liên kết với thực thể Doctor

    @Column(name = "appointment_date", nullable = false) // Maps the field to a column and makes it non-nullable
    private Date appointmentDate; // Ngày khám theo lịch khách chọn

    @Column(name = "symptom", columnDefinition = "TEXT") // Maps the field to a column, using TEXT for potentially long descriptions
    private String symptom; // Triệu chứng mô tả

    @Column(name = "specialization") // Maps the field to a column
    private String specialization; // Chuyên khoa

    @Column(name = "mode", nullable = false) // Maps the field to a column and makes it non-nullable
    private String mode; // Online hoặc Offline

    @Column(name = "notes", columnDefinition = "TEXT") // New field for "Ghi chú" (Notes)
    private String notes; // Thêm trường ghi chú từ form
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitType visitType; // REGISTRATION, APPOINTMENT

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slot slot;

}
