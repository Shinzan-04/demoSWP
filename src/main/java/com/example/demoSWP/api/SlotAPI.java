package com.example.demoSWP.api;

import com.example.demoSWP.dto.SlotDTO;
import com.example.demoSWP.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotAPI {
    @Autowired
    private SlotService slotService;

    @GetMapping
    public List<SlotDTO> getAllSlots() {
        return slotService.getAllSlots();
    }

    @PostMapping
    public SlotDTO createSlot(@RequestBody SlotDTO request) {
        return slotService.createSlot(request);
    }

    @PutMapping("/{slotId}")
    public SlotDTO updateSlot(@PathVariable Long slotId, @RequestBody SlotDTO request) {
        return slotService.updateSlot(slotId, request);
    }

    @DeleteMapping("/{slotId}")
    public void deleteSlot(@PathVariable Long slotId) {
        slotService.deleteSlot(slotId);
    }
    // Lấy danh sách slot trống theo bác sĩ và ngày
    @GetMapping("/available-slots")
    public ResponseEntity<List<SlotDTO>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam(required = false) String date
    ) {
        LocalDate localDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        List<SlotDTO> slots = slotService.getAvailableSlotsByDoctorAndDate(doctorId, localDate);
        return ResponseEntity.ok(slots);
    }
    @GetMapping("/available-dates")
    public ResponseEntity<List<String>> getAvailableDates(
            @RequestParam Long doctorId
    ) {
        List<LocalDate> dates = slotService.getAvailableDatesByDoctor(doctorId);
        List<String> result = dates.stream()
                .map(LocalDate::toString)
                .toList();
        return ResponseEntity.ok(result);
    }

}
