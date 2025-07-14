package com.example.demoSWP.repository;

import com.example.demoSWP.entity.ARVRegimen;
import com.example.demoSWP.entity.Reminder;
import com.example.demoSWP.enums.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    // Lấy danh sách nhắc nhở theo trạng thái và trước thời điểm hiện tại (dùng trong cron job)
    List<Reminder> findByStatusAndReminderDateBefore(ReminderStatus status, LocalDateTime now);

    // Truy tìm theo customer thông qua ARV Regimen
    List<Reminder> findByArvRegimen_Customer_CustomerID(Long customerID);

    // Lấy nhắc nhở theo khoảng thời gian và trạng thái
    List<Reminder> findByReminderDateBetweenAndStatus(LocalDateTime start, LocalDateTime end, ReminderStatus status);

    // Lấy nhắc nhở trong ngày cho 1 bệnh nhân và trạng thái cụ thể
    List<Reminder> findByArvRegimen_Customer_CustomerIDAndReminderDateBetweenAndStatus(
            Long customerID,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            ReminderStatus status
    );
    List<Reminder> findByArvRegimen_ArvRegimenId(Long arvRegimenId);
    boolean existsByArvRegimenAndReminderDate(ARVRegimen regimen, LocalDateTime date);
   

}