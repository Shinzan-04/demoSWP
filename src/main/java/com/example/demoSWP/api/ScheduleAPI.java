package com.example.demoSWP.api;

import com.example.demoSWP.entity.Schedule;
import com.example.demoSWP.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "http://localhost:3000")
public class ScheduleAPI {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public List<Schedule> getAll() {
        return scheduleService.getAll();
    }

    @GetMapping("/{id}")
    public Schedule getById(@PathVariable Long id) {
        return scheduleService.getById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch với ID: " + id));
    }

    @PostMapping
    public Schedule create(@RequestBody Schedule schedule) {
        return scheduleService.create(schedule);
    }

    @PutMapping("/{id}")
    public Schedule update(@PathVariable Long id, @RequestBody Schedule schedule) {
        return scheduleService.update(id, schedule);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        scheduleService.delete(id);
    }
    @GetMapping("/doctor/{doctorId}")
    public List<Schedule> getSchedulesByDoctor(@PathVariable Long doctorId) {
        return scheduleService.getByDoctorId(doctorId);
    }


}
