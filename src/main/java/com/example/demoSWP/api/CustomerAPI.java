package com.example.demoSWP.api;

import com.example.demoSWP.dto.CustomerDTO;
import com.example.demoSWP.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @GetMapping("/me")
    public CustomerDTO getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerService.getByEmail(email);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomerDTO updateCustomerProfile(
            @PathVariable Long id,
            @RequestPart("customer") CustomerDTO customerDto,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
    ) throws IOException {
        return customerService.updateCustomerWithAvatar(id, customerDto, avatarFile);
    }
}
