package com.example.demoSWP.repository;

import com.example.demoSWP.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    // Thêm các phương thức tìm kiếm tùy chỉnh nếu cần
}