package com.example.demoSWP.api;

import com.example.demoSWP.dto.CustomerDTO;
import com.example.demoSWP.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin("*")
public class CustomerAPI {

    @Autowired
    private CustomerService customerService;

    // GET /api/customers/by-email?email=abc@gmail.com
    @GetMapping("/by-email")
    public CustomerDTO getCustomerByEmail(@RequestParam String email) {
        return customerService.getCustomerDTOByEmail(email);
    }
}
