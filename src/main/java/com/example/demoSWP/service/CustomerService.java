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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<CustomerDTO> getAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public Optional<CustomerDTO> getById(Long id) {
        return customerRepository.findById(id)
                .map(this::toDTO);
    }

    public CustomerDTO getByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với email: " + email));
    }

    public CustomerDTO updateCustomerWithAvatar(Long id, CustomerDTO dto, MultipartFile avatarFile) throws IOException {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));

        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());

        if (dto.getDateOfBirth() != null) {
            customer.setDateOfBirth((dto.getDateOfBirth()));
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String filename = UUID.randomUUID().toString() + "_" + avatarFile.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/uploads";

            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            File destFile = new File(uploadPath, filename);
            avatarFile.transferTo(destFile);

            customer.setAvatarUrl("/uploads/" + filename); // Đường dẫn tương đối
        }

        customerRepository.save(customer);
        return toDTO(customer);
    }

    private CustomerDTO toDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }
}
