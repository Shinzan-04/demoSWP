package com.example.demoSWP.repository;

import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.Registration;
import com.example.demoSWP.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

@Repository // Indicates that this interface is a Spring Data JPA repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    // JpaRepository provides basic CRUD operations (save, findById, findAll, deleteById, etc.)
    // No need to write implementation for these methods.
    long countBySlot(Slot slot);

}


