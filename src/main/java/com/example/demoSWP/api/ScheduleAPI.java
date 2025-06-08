package com.example.demoSWP.api;

import com.example.demoSWP.dto.ScheduleDTO;
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
    public List<ScheduleDTO> getAll() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/{id}")
    public ScheduleDTO getById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch với id " + id));
    }

    @PostMapping
    public ScheduleDTO create(@RequestBody ScheduleDTO dto) {
        return scheduleService.createSchedule(dto);
    }

    @PutMapping("/{id}")
    public ScheduleDTO update(@PathVariable Long id, @RequestBody ScheduleDTO dto) {
        return scheduleService.updateSchedule(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
    }
}
