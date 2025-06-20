package com.example.demoSWP.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    int rating;

    String comment;

    LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    Customer customer;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @JsonIgnore
    Doctor doctor;

}
