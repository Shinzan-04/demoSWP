package com.example.demoSWP.repository;

import com.example.demoSWP.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findBySchedule_ScheduleId(Long scheduleId); // lấy danh sách slot theo schedule

    long countBySlotId(Long slotId); // nếu cần kiểm tra nhanh số lượng
    @Query("SELECT s FROM Slot s WHERE s.schedule.doctor.doctorId = :doctorId AND s.schedule.date = :date AND s.isAvailable = true")
    List<Slot> findAvailableByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    // SlotRepository.java
    @Query("SELECT DISTINCT s.schedule.date FROM Slot s WHERE s.schedule.doctor.doctorId = :doctorId AND s.isAvailable = true")
    List<LocalDate> findAvailableDatesByDoctor(@Param("doctorId") Long doctorId);


}
