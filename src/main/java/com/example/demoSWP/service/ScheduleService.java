package com.example.demoSWP.service;

import com.example.demoSWP.entity.Schedule;
import com.example.demoSWP.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> getById(Long id) {
        return scheduleRepository.findById(id);
    }

    public Schedule create(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public Schedule update(Long id, Schedule updatedSchedule) {
        return scheduleRepository.findById(id).map(s -> {
            s.setDate(updatedSchedule.getDate());
            s.setStartTime(updatedSchedule.getStartTime());
            s.setDoctor(updatedSchedule.getDoctor());
            return scheduleRepository.save(s);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy lịch với ID: " + id));
    }

    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }
    public List<Schedule> getByDoctorId(Long doctorId) {
        return scheduleRepository.findByDoctorDoctorId(doctorId);
    }

}
