package com.example.demoSWP.api;

import com.example.demoSWP.dto.CustomerDTO;
import com.example.demoSWP.entity.Account;
import com.example.demoSWP.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerAPI {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public List<CustomerDTO> getAll() {
        return customerService.getAll();
    }

    @GetMapping("/{id}")
    public CustomerDTO getById(@PathVariable Long id) {
        return customerService.getById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));
    }

    @PostMapping
    public CustomerDTO create(@RequestBody CustomerDTO dto) {
        return customerService.create(dto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomerDTO updateCustomerProfile(
            @PathVariable Long id,
            @RequestPart("customer") CustomerDTO customerDto,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        return customerService.updateCustomerWithAvatar(id, customerDto, avatarFile);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }

    @GetMapping("/me")
    public CustomerDTO getMyProfile() {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (account == null || account.getCustomer() == null) {
            throw new RuntimeException("Không tìm thấy hồ sơ khách hàng.");
        }

        return CustomerDTO.formEntity(account.getCustomer());
    }

    @PutMapping(value = "/update-no-avatar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CustomerDTO updateCustomerNoAvatar(
            @PathVariable Long id,
            @RequestBody CustomerDTO customerDto
    ) {
        return customerService.update(id, customerDto);
    }

    @GetMapping("/with-avatar")
    public ResponseEntity<List<CustomerDTO>> getCustomersWithAvatar() {
        List<CustomerDTO> customersWithAvatar = customerService.getAllCustomersWithAvatar();
        return ResponseEntity.ok(customersWithAvatar);
    }
}
