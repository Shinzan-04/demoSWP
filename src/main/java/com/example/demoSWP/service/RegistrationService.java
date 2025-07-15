package com.example.demoSWP.service;

import com.example.demoSWP.dto.AppointmentRequest;
import com.example.demoSWP.dto.EmailDetail;
import com.example.demoSWP.dto.RegistrationRequest;
import com.example.demoSWP.dto.RegistrationResponse;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.Registration;
import com.example.demoSWP.entity.Slot;
import com.example.demoSWP.enums.Gender;
import com.example.demoSWP.enums.VisitType;
import com.example.demoSWP.exception.RegistrationNotFoundException;
import com.example.demoSWP.repository.RegistrationRepository;
import com.example.demoSWP.repository.DoctorRepository;
import com.example.demoSWP.repository.SlotRepository;
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
    private SlotRepository slotRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    public Registration saveRegistrationFromRequest(RegistrationRequest request) {
        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot không tồn tại"));

        Doctor doctor = slot.getSchedule().getDoctor();

        long currentCount = registrationRepository.countBySlot(slot);
        if (currentCount >= doctor.getMaxRegistrationsPerSlot()) {
            throw new RuntimeException("Slot đã đầy. Vui lòng chọn khung giờ khác.");
        }

        Registration registration = new Registration();
        registration.setEmail(request.getEmail());
        registration.setPhone(request.getPhone());
        registration.setSpecialization(request.getSpecialization());
        registration.setAppointmentDate(request.getAppointmentDate());
        registration.setSlot(slot);
        registration.setDoctor(doctor);
        registration.setMode(request.getMode());
        registration.setNotes(request.getNotes());
        registration.setSymptom(request.getSymptom());
        registration.setVisitType(request.getVisitType());
        // 📌 Nếu là APPOINTMENT thì gán gender = OTHER
        if (request.getVisitType() == VisitType.APPOINTMENT) {
            registration.setGender(Gender.OTHER);
        } else {
            // Ngược lại (REGISTRATION) thì dùng giá trị từ request
            registration.setGender(request.getGender());
        }
        registration.setStatus(true);

        // ✅ Nếu là REGISTRATION thì yêu cầu thông tin thêm
        if (request.getVisitType() == VisitType.REGISTRATION) {
            registration.setFullName(request.getFullName());


            registration.setDateOfBirth(request.getDateOfBirth());
            registration.setAddress(request.getAddress());
        }
        Registration saved = registrationRepository.save(registration);
        // 🔔 Gửi email xác nhận
        try {
            emailService.sendEmail(buildConfirmationEmail(saved));
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gửi email xác nhận: " + e.getMessage());
        }

        return saved;
    }




    public List<RegistrationResponse> getAllRegistrations() {
        List<Registration> registrations = registrationRepository.findAll();
        return registrations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RegistrationResponse> getAllRegistrationsWithStatus() {
        return registrationRepository.findByStatusTrue().stream()
                .map(this::convertToDTO)
                .toList();
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
    public RegistrationResponse convertToDTO(Registration reg) {
        RegistrationResponse dto = new RegistrationResponse();
        dto.setRegistrationId(reg.getRegistrationID());
        dto.setFullName(reg.getFullName());
        dto.setEmail(reg.getEmail());
        dto.setPhone(reg.getPhone());
        dto.setSpecialization(reg.getSpecialization());
        dto.setSymptom(reg.getSymptom());
        dto.setMode(reg.getMode());
        dto.setDateOfBirth(reg.getDateOfBirth());
        dto.setAddress(reg.getAddress());
        dto.setNotes(reg.getNotes());
        dto.setGender(reg.getGender().name());
        dto.setVisitType(reg.getVisitType());
        dto.setStatus(reg.isStatus());
        dto.setDoctorId(reg.getDoctor().getDoctorId());

        if (reg.getDoctor() != null) {
            dto.setDoctorName(reg.getDoctor().getFullName());
        }
        if (reg.getSlot() != null) {
            dto.setSlotId(reg.getSlot().getSlotId());
            dto.setStartTime(reg.getSlot().getStartTime());
            dto.setEndTime(reg.getSlot().getEndTime());
            dto.setAppointmentDate(reg.getSlot().getSchedule().getDate());
        }

        return dto;
    }

    public Registration updateStatus(Long id, boolean status) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationNotFoundException("Không tìm thấy đăng ký với ID: " + id));
        registration.setStatus(status);
        return registrationRepository.save(registration);
    }
    private EmailDetail buildConfirmationEmail(Registration registration) {
        EmailDetail email = new EmailDetail();
        email.setRecevierEmailRegistration(registration.getEmail());
        email.setSubject("Xác nhận đăng ký khám bệnh");
        email.setHeaderNote("Bạn đã đăng ký khám thành công!");

        // Xác định địa chỉ hiển thị
        String displayAddress;
        if (registration.getVisitType() == VisitType.REGISTRATION) {
            displayAddress = "123 Đường Y tế, Quận 1, TP.HCM";

        } else {
            displayAddress = "Bác sĩ sẽ liên hệ với bạn";
        }

        // Nội dung chính (HTML)
        StringBuilder content = new StringBuilder();
        content.append("Xin chào <strong>")
                .append(registration.getFullName() != null ? registration.getFullName() : "Quý khách")
                .append("</strong>,<br><br>");
        content.append("Cảm ơn bạn đã đăng ký khám bệnh. Dưới đây là thông tin chi tiết:<br><br>");
        content.append("🔹 <strong>Bác sĩ:</strong> ")
                .append(registration.getDoctor().getFullName()).append("<br>");
        content.append("🔹 <strong>Ngày khám:</strong> ")
                .append(registration.getAppointmentDate()).append("<br>");
        content.append("🔹 <strong>Giờ khám:</strong> ")
                .append(registration.getSlot().getStartTime())
                .append(" - ")
                .append(registration.getSlot().getEndTime()).append("<br>");
        content.append("🔹 <strong>Địa chỉ khám:</strong> ")
                .append(displayAddress).append("<br>");
        content.append("🔹 <strong>Triệu chứng:</strong> ")
                .append(registration.getSymptom() != null ? registration.getSymptom() : "Không")
                .append("<br>");
        content.append("🔹 <strong>Ghi chú:</strong> ")
                .append(registration.getNotes() != null ? registration.getNotes() : "Không")
                .append("<br><br>");
        content.append("Hãy đến đúng giờ và mang theo giấy tờ tùy thân.");

        email.setMessage(content.toString());
        email.setSubMessage(null);
        email.setButton(null);
        email.setLink(null);
        email.setFooterText("Nếu bạn cần hỗ trợ, vui lòng phản hồi email này hoặc liên hệ tổng đài.");
        email.setTemplate("emailtemplate02");

        return email;
    }

}
