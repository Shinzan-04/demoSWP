package com.example.demoSWP.service;

import com.example.demoSWP.entity.Reminder;
import com.example.demoSWP.enums.ReminderStatus;
import com.example.demoSWP.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.demoSWP.enums.ReminderStatus.PENDING;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;

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
}
