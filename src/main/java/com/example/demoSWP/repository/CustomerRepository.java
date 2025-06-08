// src/main/java/com/example/demoSWP/repository/CustomerRepository.java
package com.example.demoSWP.repository;

import com.example.demoSWP.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhone(String phone); // Keep this if still needed
    Optional<Customer> findByEmail(String email); // Added for finding customer by email
    Optional<Customer> findByCustomerID(Long customerID);
}
