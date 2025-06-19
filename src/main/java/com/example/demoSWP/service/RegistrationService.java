package com.example.demoSWP.service;

import com.example.demoSWP.dto.AppointmentRequest;
import com.example.demoSWP.dto.RegistrationRequest;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.Registration;
import com.example.demoSWP.enums.VisitType;
import com.example.demoSWP.exception.RegistrationNotFoundException;
import com.example.demoSWP.repository.RegistrationRepository;
import com.example.demoSWP.repository.DoctorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Registration saveRegistrationFromRequest(Object request) {
        Registration registration = new Registration();
        Doctor doctor;

        if (request instanceof RegistrationRequest reg) {
            doctor = doctorRepository.findById(reg.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID: " + reg.getDoctorId()));

            registration.setFullName(reg.getFullName());
            registration.setEmail(reg.getEmail());
            registration.setPhone(reg.getPhone());
            registration.setGender(reg.getGender());
            registration.setDateOfBirth(reg.getDateOfBirth());
            registration.setAddress(reg.getAddress());
            registration.setSpecialization(reg.getSpecialization());
            registration.setAppointmentDate(reg.getAppointmentDate());
            registration.setSession(reg.getSession());
            registration.setSymptom(reg.getSymptom());
            registration.setNotes(reg.getNotes());
            registration.setMode(reg.getMode()); // ✅ FE phải gửi
            registration.setVisitType(VisitType.REGISTRATION);
            registration.setDoctor(doctor);

        } else if (request instanceof AppointmentRequest app) {
            doctor = doctorRepository.findById(app.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID: " + app.getDoctorId()));

            registration = new Registration();

            registration.setEmail(app.getEmail());
            registration.setPhone(app.getPhone());
            registration.setSpecialization(app.getSpecialization());
            registration.setAppointmentDate(app.getAppointmentDate());
            registration.setSession(app.getSession());
            registration.setSymptom(app.getSymptom());
            registration.setNotes(app.getNotes());

            registration.setMode("Online");

            registration.setVisitType(VisitType.APPOINTMENT);
            registration.setDoctor(doctor);


        } else {
            throw new IllegalArgumentException("Loại dữ liệu không hợp lệ");
        }

        return registrationRepository.save(registration);
    }

    public List<RegistrationRequest> getAllRegistrations() {
        return registrationRepository.findAll().stream()
                .filter(r -> !r.isCompleted())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    private RegistrationRequest mapToDTO(Registration registration) {
        RegistrationRequest dto = modelMapper.map(registration, RegistrationRequest.class);
        if (registration.getDoctor() != null) {
            dto.setDoctorId(registration.getDoctor().getDoctorId()); // mapping thủ công nếu cần
        }
        return dto;
    }

    public Optional<Registration> getRegistrationById(Long id) {
        return registrationRepository.findById(id);
    }

    public Registration updateRegistration(Long id, RegistrationRequest reg) {
        Registration existing = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationNotFoundException("Không tìm thấy đăng ký với ID: " + id));

        Doctor doctor = doctorRepository.findById(reg.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID: " + reg.getDoctorId()));

        existing.setFullName(reg.getFullName());
        existing.setEmail(reg.getEmail());
        existing.setPhone(reg.getPhone());
        existing.setGender(reg.getGender());
        existing.setDateOfBirth(reg.getDateOfBirth());
        existing.setAddress(reg.getAddress());
        existing.setSpecialization(reg.getSpecialization());
        existing.setAppointmentDate(reg.getAppointmentDate());
        existing.setSession(reg.getSession());
        existing.setSymptom(reg.getSymptom());
        existing.setNotes(reg.getNotes());
        existing.setMode(reg.getMode());
        existing.setVisitType(VisitType.REGISTRATION);
        existing.setDoctor(doctor);

        return registrationRepository.save(existing);
    }

    public void deleteRegistration(Long id) {
        if (!registrationRepository.existsById(id)) {
            throw new RegistrationNotFoundException("Không tìm thấy đăng ký với ID: " + id);
        }
        registrationRepository.deleteById(id);
    }
    public void markAsCompleted(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationNotFoundException("Không tìm thấy đăng ký với ID: " + id));
        registration.setCompleted(true); // ✅ Chính xác
        registrationRepository.save(registration);
    }

}
