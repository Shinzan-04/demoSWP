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

    // ✅ Lấy tất cả khách hàng
    public List<CustomerDTO> getAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ✅ Tìm theo ID
    public Optional<CustomerDTO> getById(Long id) {
        return customerRepository.findById(id)
                .map(this::toDTO);
    }

    // ✅ Tìm theo email
    public CustomerDTO getByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với email: " + email));
    }

    // ✅ Tạo khách hàng mới
    public CustomerDTO create(CustomerDTO dto) {
        Customer customer = modelMapper.map(dto, Customer.class);
        return toDTO(customerRepository.save(customer));
    }

    // ✅ Cập nhật KHÔNG có avatar
    public CustomerDTO update(Long id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));

        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());

        if (dto.getDateOfBirth() != null) {
            customer.setDateOfBirth(dto.getDateOfBirth());
        }

        return toDTO(customerRepository.save(customer));
    }

    // ✅ Cập nhật CÓ avatar
    public CustomerDTO updateCustomerWithAvatar(Long id, CustomerDTO dto, MultipartFile avatarFile) {
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
            try {
                String filename = UUID.randomUUID().toString() + "_" + avatarFile.getOriginalFilename();
                String uploadDir = System.getProperty("user.dir") + "/uploads";

                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }

                File destFile = new File(uploadPath, filename);
                avatarFile.transferTo(destFile);

                customer.setAvatarUrl("/uploads/" + filename);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi lưu avatar: " + e.getMessage(), e);
            }
        }

        customerRepository.save(customer);
        return toDTO(customer);
    }


    // ✅ Xóa khách hàng
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy khách hàng với ID: " + id);
        }
        customerRepository.deleteById(id);
    }

    // ✅ Trả danh sách khách hàng có avatar
    public List<CustomerDTO> getAllCustomersWithAvatar() {
        return customerRepository.findAll()
                .stream()
                .filter(c -> c.getAvatarUrl() != null && !c.getAvatarUrl().isEmpty())
                .map(this::toDTO)
                .toList();
    }

    // ✅ Chuyển từ Entity sang DTO
    private CustomerDTO toDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }
}
