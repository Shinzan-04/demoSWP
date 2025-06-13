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
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DoctorRepository doctorRepository;



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
        return toDTO(scheduleRepository.save(schedule));
    }

    public ScheduleDTO updateSchedule(Long id, ScheduleDTO dto) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow();
        schedule.setTitle(dto.getTitle());
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
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setRoom(entity.getRoom());
        dto.setPatientName(entity.getPatientName());
        dto.setDoctorId(entity.getDoctor().getDoctorId());
        return dto;
    }

    private Schedule toEntity(ScheduleDTO dto) {
        Schedule entity = new Schedule();
        entity.setScheduleId(dto.getScheduleId());
        entity.setTitle(dto.getTitle());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setRoom(dto.getRoom());
        entity.setPatientName(dto.getPatientName());
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow();
        entity.setDoctor(doctor);
        return entity;
    }
}
