package com.example.demoSWP.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlogPostDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String tag;
    private DoctorDTO doctor;

    public BlogPostDTO() {}

    public BlogPostDTO(Long id, String title, String content, LocalDateTime createdAt, String tag, DoctorDTO doctor) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.tag = tag;
        this.doctor = doctor;
    }

    // Getters & Setters
}
