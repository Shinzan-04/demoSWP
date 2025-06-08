package com.example.demoSWP.dto;

import com.example.demoSWP.enums.Gender;
import com.example.demoSWP.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AccountResponse {
    public String email;
    public String phone;
    public Gender gender;
    public Role role;

    public String token;

}
