package com.example.demoSWP.api;
import com.example.demoSWP.entity.Account;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.demoSWP.dto.ARVAndHistoryDTO;
import com.example.demoSWP.dto.ARVRegimenDTO;
import com.example.demoSWP.service.ARVRegimenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arv-regimens")
@CrossOrigin(origins = "http://localhost:3000")
public class ARVRegimenAPI {

    @Autowired
    private ARVRegimenService arvRegimenService;

    @GetMapping
    public ResponseEntity<List<ARVRegimenDTO>> getAll() {
        try {
            List<ARVRegimenDTO> data = arvRegimenService.getAllRegimens();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            System.err.println("❌ Lỗi getAll ARVRegimens: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // ARVRegimenAPI.java
    @GetMapping("/my-regimens")
    public ResponseEntity<List<ARVRegimenDTO>> getMyARVRegimens() {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (account == null || account.getCustomer() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long customerId = account.getCustomer().getCustomerID();
        List<ARVRegimenDTO> regimens = arvRegimenService.getByCustomerId(customerId);
        return ResponseEntity.ok(regimens);
    }



    @PostMapping("/with-history")
    public ResponseEntity<?> createARVWithHistory(@RequestBody ARVAndHistoryDTO dto) {
        arvRegimenService.saveARVWithMedicalHistory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update-with-history")
    public ResponseEntity<?> updateARVWithHistory(@RequestBody ARVAndHistoryDTO dto) {
        arvRegimenService.updateARVWithMedicalHistory(dto);
        return ResponseEntity.ok("Cập nhật thành công");
    }

    @PostMapping
    public ResponseEntity<ARVRegimenDTO> create(@RequestBody ARVRegimenDTO dto) {
        return ResponseEntity.ok(arvRegimenService.createOrUpdate(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ARVRegimenDTO> update(@PathVariable Long id, @RequestBody ARVRegimenDTO dto) {
        dto.setArvRegimenId(id);
        return ResponseEntity.ok(arvRegimenService.createOrUpdate(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ARVRegimenDTO> getById(@PathVariable Long id) {
        ARVRegimenDTO dto = arvRegimenService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        arvRegimenService.delete(id);
        return ResponseEntity.ok().build();
    }
}
