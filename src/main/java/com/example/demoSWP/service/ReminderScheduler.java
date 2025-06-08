package com.example.demoSWP.service;

import com.example.demoSWP.entity.Reminder;
import com.example.demoSWP.enums.ReminderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderService reminderService;

    @Scheduled(cron = "*/10 * * * * *") // mỗi 10 giây → dễ kiểm tra

    public void sendDailyReminders() {
        List<Reminder> reminders = reminderService.getRemindersForToday();

        for (Reminder reminder : reminders) {
            // Giả lập gửi tin nhắn/log
            System.out.println("Gửi nhắc nhở đến bệnh nhân: " + reminder.getCustomer().getFullName());
            System.out.println("Nội dung: " + reminder.getReminderContent());

            // Đánh dấu là đã gửi
            reminder.setStatus(ReminderStatus.SENT);
            reminderService.createReminder(reminder); // update
        }
    }
}
