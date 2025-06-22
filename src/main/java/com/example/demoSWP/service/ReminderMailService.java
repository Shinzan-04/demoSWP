package com.example.demoSWP.service;

import com.example.demoSWP.dto.EmailDetail;
import com.example.demoSWP.entity.ARVRegimen;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.entity.Reminder;
import com.example.demoSWP.enums.ReminderStatus;
import com.example.demoSWP.repository.ARVRegimenRepository;
import com.example.demoSWP.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderMailService {

    private final ReminderService reminderService;
    private final EmailService emailService;
    private final ARVRegimenRepository arvRegimenRepository;
    private final ReminderRepository reminderRepository;

    // Test cron mỗi 10 giây
    @Scheduled(cron = "*/10 * * * * *")
    // Prod cron 8h sáng mỗi ngày
    // @Scheduled(cron = "0 0 8 * * *")
    public void generateAndSendReminderDaily() {
        LocalDate today = LocalDate.now();

        List<ARVRegimen> regimens = arvRegimenRepository.findAllByEndDateGreaterThanEqual(today);

        for (ARVRegimen regimen : regimens) {
            // Nếu đã có reminder hôm nay → bỏ qua
            boolean exists = reminderRepository.existsByArvRegimenAndReminderDate(regimen, today.atTime(8, 0));
            if (exists) continue;

            Reminder reminder = new Reminder();
            reminder.setArvRegimen(regimen);
            reminder.setReminderDate(today.atTime(8, 0));
            reminder.setStatus(ReminderStatus.PENDING);

            // Chuẩn bị email
            EmailDetail email = new EmailDetail();
            email.setReceiver(regimen.getCustomer().getAccount());

            if (today.isEqual(regimen.getEndDate())) {
                // Ngày cuối cùng → gửi lời nhắc tái khám
                reminder.setReminderContent("📌 Hôm nay là ngày cuối cùng của liệu trình ARV. Mời bạn tái khám.");
                email.setSubject("📌 Kết thúc liệu trình ARV – Mời tái khám");
                email.setHeaderNote("Phác đồ điều trị đã kết thúc");
                email.setMessage("Hôm nay là ngày cuối cùng theo kế hoạch điều trị.");
                email.setSubMessage("Vui lòng đặt lịch tái khám để được theo dõi tiếp.");
                email.setLink("http://your-app.com/booking");
                email.setButton("Đặt lịch khám");
                email.setFooterText("Cảm ơn bạn đã tuân thủ điều trị.");
                reminder.setStatus(ReminderStatus.DONE);
            } else {
                // Ngày bình thường
                reminder.setReminderContent("💊 Nhắc nhở uống thuốc ARV ngày " + today);
                email.setSubject("💊 Nhắc nhở uống thuốc ARV");
                email.setHeaderNote("Uống thuốc đúng giờ");
                email.setMessage("Bạn cần uống thuốc ARV theo đúng phác đồ hôm nay.");
                email.setSubMessage("Giữ đúng lịch trình để đạt hiệu quả.");
                email.setLink("http://your-app.com/reminder");
                email.setButton("Xem nhắc nhở");
                email.setFooterText("Chúc bạn một ngày tốt lành.");
                reminder.setStatus(ReminderStatus.SENT);
            }

            // Gửi mail và lưu reminder
            try {
                emailService.sendEmail(email);
                reminderRepository.save(reminder);
                System.out.println("✅ Reminder sent to " + regimen.getCustomer().getEmail());
            } catch (Exception e) {
                System.err.println("❌ Gửi thất bại: " + e.getMessage());
            }
        }
    }
}
