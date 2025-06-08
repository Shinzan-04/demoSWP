package com.example.demoSWP.enums;

public enum Role {
    USER,
    ADMIN,
    DOCTOR,
    STAFF;

    // Có thể thêm các method utility nếu cần
    public String getDisplayName() {
        return this.name().toLowerCase();
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isDoctor() {
        return this == DOCTOR;
    }

    public boolean isUser() {
        return this == USER;
    }

    public boolean isStaff() {
        return this == STAFF;
    }
}