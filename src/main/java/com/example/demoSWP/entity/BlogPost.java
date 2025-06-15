
package com.example.demoSWP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // ✅ Cho phép nội dung dài với MEDIUMTEXT
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    private LocalDateTime createdAt;

    private String tag;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
}
