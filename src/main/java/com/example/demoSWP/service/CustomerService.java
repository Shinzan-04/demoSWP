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

    // Update with optional avatar upload, enhanced to match DoctorService style
    public CustomerDTO updateCustomerWithAvatar(Long id, CustomerDTO dto, MultipartFile avatarFile) throws IOException {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));

        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());

        if (dto.getDateOfBirth() != null) {
            dto.setDateOfBirth(customer.getDateOfBirth()); // ✅

        }

        // If new avatar file is present and not empty, save and update avatarUrl
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String fileUrl = saveAvatarFile(avatarFile);
            customer.setAvatarUrl(fileUrl);
        }

        customerRepository.save(customer);
        return toDTO(customer);
    }

    private CustomerDTO toDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }

    public String saveAvatarFile(MultipartFile file) throws IOException {
        // Use same upload path concept as DoctorService
        String uploadDir = System.getProperty("user.dir") + "/uploads";

        System.out.println("===> Upload path: " + uploadDir);

        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            boolean created = uploadFolder.mkdirs();
            System.out.println("===> Created upload folder: " + created);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destination = new File(uploadFolder, filename);

        System.out.println("===> Destination path: " + destination.getAbsolutePath());

        file.transferTo(destination);

        return "/uploads/" + filename;
    }
}

