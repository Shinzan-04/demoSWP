package com.example.demoSWP.service;

import com.example.demoSWP.dto.EmailDetail;
import com.example.demoSWP.entity.ARVRegimen;
import com.example.demoSWP.entity.Reminder;
import com.example.demoSWP.enums.ReminderStatus;
import com.example.demoSWP.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.demoSWP.enums.ReminderStatus.PENDING;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private EmailService emailService;

    // Tạo nhắc nhở
    public Reminder createReminder(Reminder reminder) {

        return reminderRepository.save(reminder);
    }

    // Lấy tất cả nhắc nhở của một bệnh nhân (truy qua ARV Regimen)
    public List<Reminder> getAllRemindersByCustomer(Long customerID) {
        System.out.println("DEBUG: Fetching ALL reminders for Customer ID: " + customerID);
        return reminderRepository.findByArvRegimen_Customer_CustomerID(customerID);
    }

    // Lấy nhắc nhở trong ngày cho một bệnh nhân
    public List<Reminder> getTodayRemindersByCustomer(Long customerID) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return reminderRepository.findByArvRegimen_Customer_CustomerIDAndReminderDateBetweenAndStatus(
                customerID,
                startOfDay,
                endOfDay,
                PENDING
        );
    }

    // Lấy tất cả nhắc nhở trong ngày (cho cron job)
    public List<Reminder> getRemindersForToday() {
        LocalDateTime now = LocalDateTime.now();
        return reminderRepository.findByReminderDateBetweenAndStatus(
                now.withHour(0).withMinute(0),
                now.withHour(23).withMinute(59),
                PENDING
        );
    }

    // Cập nhật trạng thái nhắc nhở
    public void updateStatus(Long id, ReminderStatus status) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found with ID: " + id));
        reminder.setStatus(status);
        reminderRepository.save(reminder);
    }

    // tự động tạo nhắc dựa trên arv (chưa dùng)
    @Transactional
    public void generateRemindersForARV(ARVRegimen regimen) {
        LocalDate start = regimen.getCreateDate();
        LocalDate end = regimen.getEndDate();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            Reminder reminder = new Reminder();
            reminder.setArvRegimen(regimen);
            reminder.setReminderDate(date.atTime(8, 0)); // 8h sáng mỗi ngày
            reminder.setReminderContent("💊 Nhắc nhở uống thuốc ARV ngày " + date);
            reminder.setStatus(ReminderStatus.PENDING);

            reminderRepository.save(reminder);
        }
    }


}
