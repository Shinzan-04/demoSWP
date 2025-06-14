package com.example.demoSWP.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@NoArgsConstructor
@Getter
@Setter
public class BlogPostDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String tag;
    private Long doctorId;
    private String doctorName;
}
