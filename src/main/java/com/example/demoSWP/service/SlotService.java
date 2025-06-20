package com.example.demoSWP.service;

import com.example.demoSWP.dto.SlotDTO;
import com.example.demoSWP.entity.Schedule;
import com.example.demoSWP.entity.Slot;
import com.example.demoSWP.repository.ScheduleRepository;
import com.example.demoSWP.repository.SlotRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlotService {
    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<SlotDTO> getAllSlots() {
        return slotRepository.findAll().stream()
                .map(slot -> {
                    SlotDTO dto = modelMapper.map(slot, SlotDTO.class);
                    dto.setScheduleId(slot.getSchedule().getScheduleId());
                    return dto;
                }).collect(Collectors.toList());
    }

    public SlotDTO createSlot(SlotDTO request) {
        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Schedule"));

        Slot slot = new Slot();
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setAvailable(request.isAvailable());
        slot.setSchedule(schedule);

        return modelMapper.map(slotRepository.save(slot), SlotDTO.class);
    }

    public SlotDTO updateSlot(Long slotId, SlotDTO request) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy slot"));

        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setAvailable(request.isAvailable());

        return modelMapper.map(slotRepository.save(slot), SlotDTO.class);
    }

    public void deleteSlot(Long slotId) {
        if (!slotRepository.existsById(slotId)) {
            throw new RuntimeException("Không tìm thấy slot");
        }
        slotRepository.deleteById(slotId);
    }
    public List<Slot> generateSlotsForSchedule(Schedule schedule) {
        List<Slot> slots = new ArrayList<>();

        LocalTime start = schedule.getStartTime();
        LocalTime end = schedule.getEndTime();
        int durationMinutes = 60;

        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Giờ bắt đầu/kết thúc không hợp lệ trong Schedule");
        }

        while (start.isBefore(end)) {
            LocalTime slotEnd = start.plusMinutes(durationMinutes);
            if (slotEnd.isAfter(end)) break;

            Slot slot = new Slot();
            slot.setStartTime(start);
            slot.setEndTime(slotEnd);
            slot.setAvailable(true);
            slot.setSchedule(schedule);
            slots.add(slot);

            // Nếu kết thúc bằng đúng end time, không tạo thêm nữa
            if (slotEnd.equals(end)) break;

            start = slotEnd;
        }

        System.out.println("✅ Tạo " + slots.size() + " slot cho scheduleId = " + schedule.getScheduleId());
        System.out.println("Thời gian từ: " + schedule.getStartTime() + " → " + schedule.getEndTime());

        return slotRepository.saveAll(slots);
    }


    public List<SlotDTO> getAvailableSlotsByDoctorAndDate(Long doctorId, LocalDate date) {
        List<Slot> slots = slotRepository.findAvailableByDoctorAndDate(doctorId, date);
        return slots.stream().map(slot -> {
            SlotDTO dto = new SlotDTO();
            dto.setSlotId(slot.getSlotId());
            dto.setStartTime(slot.getStartTime());
            dto.setEndTime(slot.getEndTime());
            dto.setAvailable(slot.isAvailable());
            dto.setScheduleId(slot.getSchedule().getScheduleId());
            dto.setDate(slot.getSchedule().getDate().toString());
            return dto;
        }).toList();
    }

    // SlotService.java
    public List<LocalDate> getAvailableDatesByDoctor(Long doctorId) {
        return slotRepository.findAvailableDatesByDoctor(doctorId);
    }

}
