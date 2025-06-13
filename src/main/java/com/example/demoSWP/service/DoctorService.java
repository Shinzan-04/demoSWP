package com.example.demoSWP.service;

import com.example.demoSWP.dto.DoctorDTO;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.repository.DoctorRepository;
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
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<DoctorDTO> getAll() {
        return doctorRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public Optional<DoctorDTO> getById(Long id) {
        return doctorRepository.findById(id)
                .map(this::toDTO);
    }

    public DoctorDTO create(DoctorDTO dto) {
        Doctor doctor = toEntity(dto);
        return toDTO(doctorRepository.save(doctor));
    }

    public DoctorDTO update(Long id, DoctorDTO dto) {
        return doctorRepository.findById(id).map(existing -> {
            existing.setFullName(dto.getFullName());
            existing.setSpecialization(dto.getSpecialization());
            existing.setPhone(dto.getPhone());
            existing.setEmail(dto.getEmail());
            existing.setWorkExperienceYears(dto.getWorkExperienceYears());
            existing.setRole(dto.getRole());
            return toDTO(doctorRepository.save(existing));
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID: " + id));
    }

    public void delete(Long id) {
        doctorRepository.deleteById(id);
    }

    private DoctorDTO toDTO(Doctor entity) {
        return modelMapper.map(entity, DoctorDTO.class);
    }

    private Doctor toEntity(DoctorDTO dto) {
        return modelMapper.map(dto, Doctor.class);
    }
    public DoctorDTO updateWithAvatar(Long id, DoctorDTO dto, MultipartFile avatarFile) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID: " + id));

        // Cập nhật thông tin
        doctor.setFullName(dto.getFullName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setPhone(dto.getPhone());
        doctor.setDescription(dto.getDescription());
        doctor.setWorkExperienceYears(dto.getWorkExperienceYears());

        // Nếu có ảnh mới
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String fileUrl = saveAvatarFile(avatarFile);
                doctor.setAvatarUrl(fileUrl);
            } catch (IOException e) {
                e.printStackTrace(); // hoặc dùng logger

                throw new RuntimeException("Lỗi khi lưu ảnh đại diện: " + e.getMessage(), e);

            }
        }

        doctorRepository.save(doctor);
        return DoctorDTO.fromEntity(doctor);
    }
    public String saveAvatarFile(MultipartFile file) throws IOException {
        // Đường dẫn thư mục uploads ngang cấp với thư mục project
        String uploadDir = System.getProperty("user.dir") + "/uploads";

        System.out.println("===> Upload path: " + uploadDir); // In ra để kiểm tra thực tế

        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            boolean created = uploadFolder.mkdirs();
            System.out.println("===> Created upload folder: " + created);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destination = new File(uploadFolder, filename);

        System.out.println("===> Destination path: " + destination.getAbsolutePath());

        file.transferTo(destination); // Có thể ném IOException nếu sai path

        return "/uploads/" + filename;
    }



}
