package com.example.demoSWP.service;

import com.example.demoSWP.dto.AppointmentDTO;
import com.example.demoSWP.entity.Appointment;
import com.example.demoSWP.repository.AppointmentRepository;
import com.example.demoSWP.repository.CustomerRepository;
import com.example.demoSWP.repository.ScheduleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<AppointmentDTO> getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::convertToDTO);
    }

    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        Appointment appointment = convertToEntity(dto);
        Appointment saved = appointmentRepository.save(appointment);
        return convertToDTO(saved);
    }

    public AppointmentDTO updateAppointment(Long id, AppointmentDTO dto) {
        return appointmentRepository.findById(id).map(existing -> {
            existing.setStatus(dto.getStatus());
            existing.setNote(dto.getNote());

            existing.setCustomer(customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy customer")));

            existing.setSchedule(scheduleRepository.findById(dto.getScheduleId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy schedule")));

            return convertToDTO(appointmentRepository.save(existing));
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn với id " + id));
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = modelMapper.map(appointment, AppointmentDTO.class);
        dto.setCustomerId(appointment.getCustomer().getCustomerID());
        dto.setScheduleId(appointment.getSchedule().getScheduleId());
        return dto;
    }

    private Appointment convertToEntity(AppointmentDTO dto) {
        Appointment appointment = modelMapper.map(dto, Appointment.class);
        appointment.setCustomer(customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy customer")));
        appointment.setSchedule(scheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy schedule")));
        return appointment;
    }
}
