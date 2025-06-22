package com.example.demoSWP.api;

import com.example.demoSWP.dto.ReminderRequest;
import com.example.demoSWP.entity.ARVRegimen;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.entity.Reminder;
import com.example.demoSWP.enums.ReminderStatus;
import com.example.demoSWP.repository.ARVRegimenRepository;
import com.example.demoSWP.repository.CustomerRepository;
import com.example.demoSWP.repository.ReminderRepository;
import com.example.demoSWP.service.EmailService;
import com.example.demoSWP.service.ReminderMailService;
import com.example.demoSWP.service.ReminderService;
import com.example.demoSWP.service.AuthenticationService; // THÊM IMPORT NÀY
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // THÊM IMPORT NÀY (nếu chưa có)
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "api")
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

    @Autowired
    private ReminderMailService reminderMailService;

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

    @PutMapping("/{reminderId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long reminderId, @RequestParam ReminderStatus status) {
        reminderService.updateStatus(reminderId, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{reminderId}/done") // Bạn có thể giữ hoặc bỏ nếu không dùng
    public ResponseEntity<Void> markReminderAsDone(@PathVariable Long reminderId) {
        reminderService.updateStatus(reminderId, ReminderStatus.DONE);
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

    //dành cho Admin để trigger chạy lại nhắc nhở nếu Cron job lỗi
    @PostMapping("/reminders/process")
    public ResponseEntity<?> sendDailyReminders() {
        reminderMailService.generateAndSendReminderDaily();
        return ResponseEntity.ok("Processed all reminders for today.");
    }

    // <--- KẾT THÚC ENDPOINT CẦN THÊM --->
}