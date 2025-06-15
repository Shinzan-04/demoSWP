package com.example.demoSWP.service;

import com.example.demoSWP.dto.CustomerDTO;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Get all customers (optional)
    public List<CustomerDTO> getAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Get one by ID
    public Optional<CustomerDTO> getById(Long id) {
        return customerRepository.findById(id)
                .map(this::toDTO);
    }

    // Get current customer profile by email (can also be in AuthenticationService)
    public CustomerDTO getByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với email: " + email));
    }

    // Update with optional avatar upload
    public CustomerDTO updateCustomerWithAvatar(Long id, CustomerDTO dto, MultipartFile avatarFile) throws IOException {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));

        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());

        if (dto.getDateOfBirth() != null) {
            customer.setDateOfBirth(java.time.LocalDate.parse(dto.getDateOfBirth()));
            // handle parsing carefully based on frontend format
        }



        customerRepository.save(customer);
        return toDTO(customer);
    }

    private CustomerDTO toDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }

    public String saveAvatarFile(MultipartFile file) throws IOException {
        String uploadDir = System.getProperty("user.dir") + "/uploads";

        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destination = new File(uploadFolder, filename);
        file.transferTo(destination);

        return "/uploads/" + filename;
    }
}
