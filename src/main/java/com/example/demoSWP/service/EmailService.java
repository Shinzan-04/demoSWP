package com.example.demoSWP.service;

import com.example.demoSWP.dto.EmailDetail;
import com.example.demoSWP.entity.Account;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;


@Service
public class EmailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(EmailDetail emailDetail) {
        try {
            String toEmail;
            String name;

            if (emailDetail.getReceiver() != null) {
                toEmail = emailDetail.getReceiver().getEmail();
                name = emailDetail.getReceiver().getFullName();
            } else if (emailDetail.getRecevierEmailRegistration() != null) {
                toEmail = emailDetail.getRecevierEmailRegistration();
                name = "Khách hàng";
            } else {
                throw new IllegalArgumentException("Không xác định được email người nhận.");
            }

            Context context = new Context();
            context.setVariable("name", emailDetail.getName());
            context.setVariable("message", emailDetail.getMessage());
            context.setVariable("subMessage", emailDetail.getSubMessage());
            context.setVariable("Link", emailDetail.getLink());
            context.setVariable("button", emailDetail.getButton());
            context.setVariable("footerText", emailDetail.getFooterText());
            context.setVariable("headerNote", emailDetail.getHeaderNote());

            String html = templateEngine.process("emailtemplate01", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom("admin@gmail.com");
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());

            javaMailSender.send(mimeMessage);
            System.out.println("✅ Đã gửi email đến: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi email: " + e.getMessage());
        }
    }
    public void sendRegisterEmail(Account newAccount) {
        EmailDetail emailDetail = new EmailDetail();

        // Thiết lập người nhận và tiêu đề mail
        emailDetail.setReceiver(newAccount);
        emailDetail.setSubject("Chào mừng bạn đến với hệ thống");

        // Các biến dùng trong template email
        emailDetail.setName(newAccount.getFullName());
        emailDetail.setMessage("Cảm ơn bạn đã đăng ký tài khoản tại hệ thống của chúng tôi.");
        emailDetail.setSubMessage("Chúng tôi rất vui được đồng hành cùng bạn trong hành trình chăm sóc sức khỏe.");
        emailDetail.setHeaderNote("Chào mừng thành viên mới");
        emailDetail.setFooterText("Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email này.");

        // Không có nút bấm trong email đăng ký, nên để null hoặc bỏ qua
        emailDetail.setButton(null);
        emailDetail.setLink(null);

        // Tên template email (phù hợp với template bạn đang dùng)
        emailDetail.setTemplate("emailtemplate02");

        // Gọi hàm gửi email
        sendEmail(emailDetail);
    }


}
