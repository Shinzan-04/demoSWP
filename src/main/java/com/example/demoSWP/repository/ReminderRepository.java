// src/main/java/com/example/demoSWP/repository/ReminderRepository.java
package com.example.demoSWP.repository;

import com.example.demoSWP.entity.Reminder;
import com.example.demoSWP.enums.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByStatusAndReminderDateBefore(ReminderStatus status, LocalDateTime now);
    // Corrected method name to match the property 'customerID' in Customer entity
    List<Reminder> findByCustomerCustomerID(Long customerID);

    List<Reminder> findByReminderDateBetweenAndStatus(LocalDateTime start, LocalDateTime end, ReminderStatus status);

    List<Reminder> findByCustomer_CustomerIDAndReminderDateBetweenAndStatus(
            Long customerID,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            ReminderStatus status
    );
}
