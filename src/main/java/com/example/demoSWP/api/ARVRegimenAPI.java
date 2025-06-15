package com.example.demoSWP.api;

import com.example.demoSWP.dto.ARVRegimenDTO;
import com.example.demoSWP.service.ARVRegimenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arv-regimens")
@CrossOrigin(origins = "*")
public class ARVRegimenAPI {

    @Autowired
    private ARVRegimenService arvRegimenService;

    @GetMapping
    public List<ARVRegimenDTO> getAll() {
        return arvRegimenService.getAllRegimens();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ARVRegimenDTO> getById(@PathVariable Long id) {
        ARVRegimenDTO dto = arvRegimenService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ARVRegimenDTO> create(@RequestBody ARVRegimenDTO dto) {
        System.out.println("🔥 Nhận được request ARV:");
        System.out.println(dto);
        return ResponseEntity.ok(arvRegimenService.createOrUpdate(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ARVRegimenDTO> update(@PathVariable Long id, @RequestBody ARVRegimenDTO dto) {
        dto.setArvRegimenId(id);
        return ResponseEntity.ok(arvRegimenService.createOrUpdate(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        arvRegimenService.delete(id);
        return ResponseEntity.ok().build();
    }
}
