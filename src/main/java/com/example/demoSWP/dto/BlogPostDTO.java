package com.example.demoSWP.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlogPostDTO {
    private Long id;
    private String title;
    private String content;
    private String tag;
    private LocalDateTime createdAt;
    private Long doctorId;
    private String doctorName;
    private String imageUrl;
    private LocalDateTime updatedAt;

}
