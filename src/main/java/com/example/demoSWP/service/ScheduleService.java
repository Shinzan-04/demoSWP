package com.example.demoSWP.service;

import com.example.demoSWP.dto.ScheduleDTO;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.Schedule;
import com.example.demoSWP.repository.DoctorRepository;
import com.example.demoSWP.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<ScheduleDTO> getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .map(this::convertToDTO);
    }

    public ScheduleDTO createSchedule(ScheduleDTO dto) {
        Schedule schedule = convertToEntity(dto);
        Schedule saved = scheduleRepository.save(schedule);
        return convertToDTO(saved);
    }

    public ScheduleDTO updateSchedule(Long id, ScheduleDTO dto) {
        return scheduleRepository.findById(id).map(existing -> {
            existing.setDate(dto.getDate());
            existing.setStartTime(dto.getStartTime());
            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với id " + dto.getDoctorId()));
            existing.setDoctor(doctor);
            Schedule updated = scheduleRepository.save(existing);
            return convertToDTO(updated);
        }).orElseThrow(() -> new RuntimeException("Schedule không tồn tại"));
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    // === Mapping ===
    private ScheduleDTO convertToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setDoctorId(schedule.getDoctor().getDoctorId());
        dto.setDate(schedule.getDate());
        dto.setStartTime(schedule.getStartTime());
        return dto;
    }

    private Schedule convertToEntity(ScheduleDTO dto) {
        Schedule schedule = new Schedule();
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với id " + dto.getDoctorId()));
        schedule.setDoctor(doctor);
        schedule.setDate(dto.getDate());
        schedule.setStartTime(dto.getStartTime());
        return schedule;
    }
}
