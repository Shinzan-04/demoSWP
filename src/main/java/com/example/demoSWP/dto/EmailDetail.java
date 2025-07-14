package com.example.demoSWP.dto;

import com.example.demoSWP.entity.Account;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class EmailDetail {
    Account receiver;
    String recevierEmailRegistration;
    String name;
    String subject;
    String link;
    private String buttonText;  // Text trên nút
    private String message;     // Nội dung chính
    private String template;
    private String subMessage;    // Nội dung phụ
    private String button;        // Text trên nút
    private String footerText;    // Lời kết ở cuối email
    private String headerNote;    // Phụ đề header (tuỳ chọn, ví dụ: "Thông báo hệ thống")

}
