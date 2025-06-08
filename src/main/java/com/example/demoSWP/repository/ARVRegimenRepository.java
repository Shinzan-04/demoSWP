// src/main/java/com/example/demoSWP/repository/ARVRegimenRepository.java
package com.example.demoSWP.repository;

import com.example.demoSWP.entity.ARVRegimen;
import com.example.demoSWP.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ARVRegimenRepository extends JpaRepository<ARVRegimen, Long> {
    // JpaRepository cung cấp các phương thức findById, findAll, save, deleteById...

    // Ví dụ phương thức tìm kiếm theo tên phác đồ
    Optional<ARVRegimen> findByRegimenName(String regimenName);

    // Ví dụ phương thức tìm kiếm theo ID khách hàng (nếu cần)
    List<ARVRegimen> findByCustomerCustomerID(Long customerId);

    Optional<ARVRegimen> findByArvRegimenId(Long id);
}
