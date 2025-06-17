package com.example.demoSWP.service;

import com.example.demoSWP.dto.ScheduleDTO;
import com.example.demoSWP.dto.SlotDTO;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.Schedule;
import com.example.demoSWP.entity.Slot;
import com.example.demoSWP.repository.DoctorRepository;
import com.example.demoSWP.repository.ScheduleRepository;
import com.example.demoSWP.repository.SlotRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SlotService slotService;

    @Autowired
    private SlotRepository slotRepository;



    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getSchedulesByDoctor(Long doctorId) {
        return scheduleRepository.findByDoctorDoctorId(doctorId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<ScheduleDTO> getScheduleById(Long id) {
        return scheduleRepository.findById(id).map(this::toDTO);
    }

    public ScheduleDTO createSchedule(ScheduleDTO dto) {
        Schedule schedule = toEntity(dto);
        schedule = scheduleRepository.save(schedule); // Lưu trước để có ID

        List<Slot> slots = slotService.generateSlotsForSchedule(schedule);
        schedule.setSlots(slots); // ⚠️ Quan trọng

        return toDTO(schedule);
    }


    public ScheduleDTO updateSchedule(Long id, ScheduleDTO dto) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow();
        schedule.setTitle(dto.getTitle());

        LocalDate date = dto.getDate() != null ? dto.getDate() : LocalDate.now();
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày không được nhỏ hơn hiện tại");
        }
        schedule.setDate(date);

        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setRoom(dto.getRoom());
        schedule.setPatientName(dto.getPatientName());

        return toDTO(scheduleRepository.save(schedule));
    }


    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    private ScheduleDTO toDTO(Schedule entity) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleId(entity.getScheduleId());
        dto.setTitle(entity.getTitle());
        dto.setDate(entity.getDate());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setRoom(entity.getRoom());
        dto.setPatientName(entity.getPatientName());
        dto.setDoctorId(entity.getDoctor().getDoctorId());
        if (entity.getSlots() != null && !entity.getSlots().isEmpty()) {
            List<SlotDTO> slotDTOs = entity.getSlots().stream()
                    .map(slot -> {
                        SlotDTO s = new SlotDTO();
                        s.setSlotId(slot.getSlotId());
                        s.setStartTime(slot.getStartTime());
                        s.setEndTime(slot.getEndTime());
                        s.setAvailable(slot.isAvailable());
                        s.setScheduleId(entity.getScheduleId());
                        return s;
                    }).toList();
            dto.setSlots(slotDTOs);
        }
        return dto;
    }



    private Schedule toEntity(ScheduleDTO dto) {
        Schedule entity = new Schedule();
        entity.setScheduleId(dto.getScheduleId());
        entity.setTitle(dto.getTitle());
//        if (dto.getDate() != null && dto.getDate().isBefore(LocalDate.now())) {
//            throw new IllegalArgumentException("Ngày không được nhỏ hơn hiện tại");
//        }

        if (dto.getDate() != null) {
            entity.setDate(dto.getDate());
        } else {
            entity.setDate(LocalDate.now()); // ✅ Gán ngày hiện tại nếu người dùng không nhập
        }

        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setRoom(dto.getRoom());
        entity.setPatientName(dto.getPatientName());
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow();
        entity.setDoctor(doctor);
        return entity;
    }

    //tự động update lịch mỗi tuần - Không cần gọi api nhưng phải mở hệ thống tại thời điểm chạy cron
        // Chạy mỗi Thứ Hai lúc 00:00
    @Transactional
    @Scheduled(cron = "0 0 0 * * MON") // mỗi thứ 2, 00:00
    public void copyLastWeekSchedules() {
        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate lastSunday = today.minusWeeks(1).with(DayOfWeek.SUNDAY);

        List<Schedule> lastWeekSchedules = scheduleRepository.findByDateBetween(lastMonday, lastSunday);

        for (Schedule oldSchedule : lastWeekSchedules) {
            LocalDate newDate = oldSchedule.getDate().plusDays(7);

            boolean exists = scheduleRepository.existsByDoctorDoctorIdAndDate(
                    oldSchedule.getDoctor().getDoctorId(), newDate
            );
            if (exists) continue; // tránh trùng lịch

            Schedule newSchedule = new Schedule();
            newSchedule.setDoctor(oldSchedule.getDoctor());
            newSchedule.setDate(newDate);
            newSchedule.setTitle(oldSchedule.getTitle());
            newSchedule.setStartTime(oldSchedule.getStartTime());
            newSchedule.setEndTime(oldSchedule.getEndTime());
            newSchedule.setRoom(oldSchedule.getRoom());
            newSchedule.setPatientName(oldSchedule.getPatientName());

            Schedule savedSchedule = scheduleRepository.save(newSchedule);

            for (Slot oldSlot : oldSchedule.getSlots()) {
                Slot newSlot = new Slot();
                newSlot.setStartTime(oldSlot.getStartTime());
                newSlot.setEndTime(oldSlot.getEndTime());
                newSlot.setAvailable(true);
                newSlot.setSchedule(savedSchedule);

                slotRepository.save(newSlot);
            }

            System.out.println("✅ Copied schedule for date " + newDate + " with " + oldSchedule.getSlots().size() + " slots");
        }

        System.out.println("✅ Completed auto-copy of weekly schedules.");
    }



}
