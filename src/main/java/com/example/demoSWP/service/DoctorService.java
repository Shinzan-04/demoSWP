package com.example.demoSWP.service;

import com.example.demoSWP.dto.DoctorDTO;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.repository.DoctorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Doctor createDoctor(DoctorDTO doctorDTO) {
        // Mapping DTO -> Entity
        Doctor doctor = modelMapper.map(doctorDTO, Doctor.class);
        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {
        return doctorRepository.findById(id).map(existingDoctor -> {
            existingDoctor.setFullName(updatedDoctor.getFullName());
            existingDoctor.setPhone(updatedDoctor.getPhone());
            existingDoctor.setEmail(updatedDoctor.getEmail());
            existingDoctor.setAccount(updatedDoctor.getAccount()); // Nếu có cập nhật account
            return doctorRepository.save(existingDoctor);
        }).orElseThrow(() -> new RuntimeException("Doctor not found with id " + id));
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }
}
