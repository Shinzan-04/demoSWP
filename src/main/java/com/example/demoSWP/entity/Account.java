package com.example.demoSWP.entity;


import com.example.demoSWP.enums.Gender;
import com.example.demoSWP.enums.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // THÊM IMPORT NÀY
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Setter
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    public String email;
    public String phone;
    public String fullName;
    public String password;


    @Enumerated(EnumType.STRING)
    public Gender gender;

    @Enumerated(EnumType.STRING)
    public Role role; // Trường này giữ vai trò của người dùng

    @JsonBackReference
    @OneToOne(mappedBy = "account")
    private Doctor doctor;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ĐÃ SỬA ĐỔI: Trả về một SimpleGrantedAuthority dựa trên vai trò của tài khoản
        // Spring Security mặc định mong đợi các vai trò có tiền tố "ROLE_"
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}