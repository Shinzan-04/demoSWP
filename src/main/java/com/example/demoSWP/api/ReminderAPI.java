package com.example.demoSWP.api;

import com.example.demoSWP.dto.ReminderRequest;
import com.example.demoSWP.entity.ARVRegimen;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.entity.Reminder;
import com.example.demoSWP.enums.ReminderStatus;
import com.example.demoSWP.repository.ARVRegimenRepository;
import com.example.demoSWP.repository.CustomerRepository;
import com.example.demoSWP.repository.ReminderRepository;
import com.example.demoSWP.service.ReminderService;
import com.example.demoSWP.service.AuthenticationService; // THÊM IMPORT NÀY
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // THÊM IMPORT NÀY (nếu chưa có)
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderAPI {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ARVRegimenRepository arvRegimenRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired // THÊM DÒNG NÀY
    private AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ReminderRequest dto) {
        if (dto.getCustomerId() == null || dto.getArvRegimenId() == null) {
            return ResponseEntity.badRequest().body("Customer ID and ARV Regimen ID must not be null");
        }

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ARVRegimen regimen = arvRegimenRepository.findByArvRegimenId(dto.getArvRegimenId())
                .orElseThrow(() -> new RuntimeException("Regimen not found"));

        Reminder reminder = new Reminder();
        reminder.setArvRegimen(regimen);
        reminder.setReminderDate(dto.getReminderDate());
        reminder.setReminderContent(dto.getReminderContent());
        reminder.setStatus(dto.getStatus());

        reminderRepository.save(reminder);

        return ResponseEntity.ok(reminder);
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Reminder>> getByCustomer(@PathVariable Long customerID) {
        return ResponseEntity.ok(reminderService.getAllRemindersByCustomer(customerID));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam ReminderStatus status) {
        reminderService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/done") // Bạn có thể giữ hoặc bỏ nếu không dùng
    public ResponseEntity<Void> markReminderAsDone(@PathVariable Long id) {
        reminderService.updateStatus(id, ReminderStatus.DONE);
        return ResponseEntity.ok().build();
    }

    // <--- THÊM TOÀN BỘ ENDPOINT NÀY VÀO ĐÂY --->
    @GetMapping("/today/me") // Endpoint mới, không cần customerId trong URL
    public ResponseEntity<List<Reminder>> getTodayRemindersForCurrentUser() {
        try {
            Long currentCustomerId = authenticationService.getCurrentCustomerId(); // Lấy ID từ token
            return ResponseEntity.ok(reminderService.getTodayRemindersByCustomer(currentCustomerId));
        } catch (RuntimeException e) { // Xử lý nếu không tìm thấy Customer
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    // --- THÊM ENDPOINT NÀY VÀO ĐÂY ---
    @GetMapping("/all/me") // Endpoint mới để lấy TẤT CẢ nhắc nhở của người dùng hiện tại
    public ResponseEntity<List<Reminder>> getAllRemindersForCurrentUser() {
        try {
            Long currentCustomerId = authenticationService.getCurrentCustomerId(); // Lấy ID từ token
            System.out.println("DEBUG: Calling /api/reminders/all/me for Customer ID: " + currentCustomerId); // Debug log
            // Gọi phương thức mới trong ReminderService để lấy tất cả nhắc nhở
            return ResponseEntity.ok(reminderService.getAllRemindersByCustomer(currentCustomerId));
        } catch (RuntimeException e) {
            System.err.println("Error fetching all reminders for current user: " + e.getMessage()); // Debug log
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    // <--- KẾT THÚC ENDPOINT CẦN THÊM --->
}