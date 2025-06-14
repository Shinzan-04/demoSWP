package com.example.demoSWP.service;

import com.example.demoSWP.dto.CustomerDTO;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public CustomerDTO getCustomerDTOByEmail(String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        return customer.map(this::mapToDTO).orElse(null);
    }

    private CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .customerID(customer.getCustomerID())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .gender(customer.getGender())
                .dateOfBirth(customer.getDateOfBirth())
                .build();
    }
}
