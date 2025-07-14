package com.example.demoSWP.api;

import com.example.demoSWP.dto.ScheduleDTO;
import com.example.demoSWP.dto.SlotDTO;
import com.example.demoSWP.service.ScheduleService;
import com.example.demoSWP.service.SlotService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedules")
@SecurityRequirement(name = "api")
@CrossOrigin(origins = "http://localhost:3000")
public class ScheduleAPI {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private SlotService slotService;

    @GetMapping
    public List<ScheduleDTO> getAll() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDTO> getById(@PathVariable Long scheduleId) {
        Optional<ScheduleDTO> dto = scheduleService.getScheduleById(scheduleId);
        return dto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doctor/{doctorId}")
    public List<ScheduleDTO> getByDoctor(@PathVariable Long doctorId) {
        return scheduleService.getSchedulesByDoctor(doctorId);
    }

    @PostMapping
    public ResponseEntity<ScheduleDTO> create(@RequestBody ScheduleDTO dto) {
        return ResponseEntity.ok(scheduleService.createSchedule(dto));
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDTO> update(@PathVariable Long scheduleId, @RequestBody ScheduleDTO dto) {
        return ResponseEntity.ok(scheduleService.updateSchedule(scheduleId, dto));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> delete(@PathVariable Long scheduleId) {
        try {
            scheduleService.deleteSchedule(scheduleId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }


    @GetMapping("/api/schedules/clone-last-week")
    public ResponseEntity<?> manualClone() {
        scheduleService.copyLastWeekSchedules();
        return ResponseEntity.ok("Đã sao chép lịch tuần trước.");
    }

}
