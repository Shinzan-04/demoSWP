package com.example.demoSWP.service;

import com.example.demoSWP.entity.Reminder;
import com.example.demoSWP.enums.ReminderStatus;
import com.example.demoSWP.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demoSWP.enums.ReminderStatus.PENDING;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;

    // Tạo nhắc nhở
    public Reminder createReminder(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    // Lấy tất cả nhắc nhở của một bệnh nhân
    public List<Reminder> getRemindersByCustomer(Long customerID) {
        // Sử dụng phương thức trực tiếp từ ReminderRepository để tìm kiếm
        return reminderRepository.findByCustomerCustomerID(customerID);
    }

    // Cập nhật trạng thái
    public void updateStatus(Long id, ReminderStatus status) {
        Reminder reminder = reminderRepository.findById(id).orElseThrow();
        reminder.setStatus(status);
        reminderRepository.save(reminder);
    }

    // Scheduler gọi dùng method này mỗi ngày
    public List<Reminder> getRemindersForToday() {
        LocalDateTime now = LocalDateTime.now();
        return reminderRepository.findByReminderDateBetweenAndStatus(
                now.withHour(0).withMinute(0),
                now.withHour(23).withMinute(59), PENDING
        );
    }

    // <--- BẮT BUỘC PHẢI THÊM TOÀN BỘ PHƯƠNG THỨC NÀY VÀO ĐÂY --->
    public List<Reminder> getTodayRemindersByCustomer(Long customerID) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return reminderRepository.findByCustomer_CustomerIDAndReminderDateBetweenAndStatus(
                customerID,
                startOfDay,
                endOfDay,
                PENDING
        );
    }

    public List<Reminder> getAllRemindersByCustomer(Long customerID) {
        System.out.println("DEBUG: Fetching ALL reminders for Customer ID (from Service): " + customerID);
        // Đây là phương thức sẽ gọi từ repository để lấy tất cả các nhắc nhở
        return reminderRepository.findByCustomerCustomerID(customerID); // Sử dụng findByCustomer_CustomerId
    }
    // <--- KẾT THÚC PHƯƠNG THỨC CẦN THÊM --->
}