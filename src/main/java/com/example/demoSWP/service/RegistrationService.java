// src/main/java/com/example/demoSWP/service/RegistrationService.java
package com.example.demoSWP.service;

import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.Registration;
import com.example.demoSWP.exception.RegistrationNotFoundException;
import com.example.demoSWP.payload.request.RegistrationRequest; // Import the RegistrationRequest DTO
import com.example.demoSWP.repository.RegistrationRepository;
import com.example.demoSWP.repository.CustomerRepository;
import com.example.demoSWP.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Indicates that this class is a service component
public class RegistrationService { // No longer an interface, directly implements logic

    private final RegistrationRepository registrationRepository;
    private final CustomerRepository customerRepository; // Inject CustomerRepository
    private final DoctorRepository doctorRepository;     // Inject DoctorRepository

    // Constructor injection for all repositories
    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository,
                               CustomerRepository customerRepository,
                               DoctorRepository doctorRepository) {
        this.registrationRepository = registrationRepository;
        this.customerRepository = customerRepository;
        this.doctorRepository = doctorRepository;
    }

    /**
     * Saves a new registration based on RegistrationRequest DTO.
     * This method handles creating/finding Customer by email and finding Doctor by email.
     * @param request The RegistrationRequest DTO containing all registration details.
     * @return The saved Registration entity.
     * @throws RuntimeException if Doctor is not found.
     */
    public Registration saveRegistrationFromRequest(RegistrationRequest request) {
        // 1. Handle Customer: Find existing by email or create new
        // Corrected: Now finding customer by email as requested
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    // If customer with this email doesn't exist, create a new one
                    Customer newCustomer = new Customer();
                    newCustomer.setFullName(request.getFullName());
                    newCustomer.setEmail(request.getEmail());
                    newCustomer.setGender(request.getGender());
                    newCustomer.setDateOfBirth(request.getDateOfBirth());
                    newCustomer.setPhone(request.getPhone()); // Keep phone for new customer
                    newCustomer.setAddress(request.getAddress());
                    return customerRepository.save(newCustomer); // Save the new customer
                });

        // 2. Handle Doctor: Find existing doctor by email
        // Corrected: Now finding doctor by email as requested, not by ID
        Doctor doctor = doctorRepository.findByFullName(request.getDoctorName())
                .orElseThrow(() -> new RuntimeException("Doctor not found with email: " + request.getDoctorName()));

        // 3. Create Registration entity from request and found Customer/Doctor
        Registration registration = new Registration();
        registration.setCustomer(customer); // Set the Customer object
        registration.setDoctor(doctor);     // Set the Doctor object
        registration.setAppointmentDate(request.getAppointmentDate());
        registration.setSession(request.getSession());
        registration.setSymptom(request.getSymptom());
        registration.setSpecialization(request.getSpecialization());
        registration.setMode(request.getMode());
        registration.setNotes(request.getNotes()); // Set the notes field

        // Save the new registration
        return registrationRepository.save(registration);
    }


    /**
     * Retrieves all registrations from the database.
     * @return A list of all registrations.
     */
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    /**
     * Retrieves a registration by its ID.
     * @param id The ID of the registration to retrieve.
     * @return An Optional containing the registration if found, otherwise empty.
     */
    public Optional<Registration> getRegistrationById(Long id) {
        return registrationRepository.findById(id);
    }

    /**
     * Updates an existing registration.
     * @param id The ID of the registration to update.
     * @param registrationDetails The registration object with updated details.
     * @return The updated registration object.
     * @throws RegistrationNotFoundException if the registration with the given ID is not found.
     */
    public Registration updateRegistration(Long id, Registration registrationDetails) {
        // Find the existing registration by ID
        Registration existingRegistration = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationNotFoundException("Registration not found with ID: " + id));

        // Update fields of the existing registration with details from registrationDetails
        existingRegistration.setCustomer(registrationDetails.getCustomer());
        existingRegistration.setDoctor(registrationDetails.getDoctor());
        existingRegistration.setAppointmentDate(registrationDetails.getAppointmentDate());
        existingRegistration.setSession(registrationDetails.getSession());
        existingRegistration.setSymptom(registrationDetails.getSymptom());
        existingRegistration.setSpecialization(registrationDetails.getSpecialization());
        existingRegistration.setMode(registrationDetails.getMode());
        existingRegistration.setNotes(registrationDetails.getNotes()); // Update notes field

        // Save the updated registration
        return registrationRepository.save(existingRegistration);
    }

    /**
     * Deletes a registration by its ID.
     * @param id The ID of the registration to delete.
     * @throws RegistrationNotFoundException if the registration with the given ID is not found.
     */
    public void deleteRegistration(Long id) {
        // Check if the registration exists before deleting
        if (!registrationRepository.existsById(id)) {
            throw new RegistrationNotFoundException("Registration not found with ID: " + id);
        }
        registrationRepository.deleteById(id);
    }
}
