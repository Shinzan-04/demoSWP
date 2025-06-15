package com.example.demoSWP.dto;

import com.example.demoSWP.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.demoSWP.entity.Customer;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private Long customerID;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Gender gender;
    private LocalDate dateOfBirth; // format as ISO string or LocalDate
    private String avatarUrl;


    public static CustomerDTO formEntity(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerID(customer.getCustomerID());
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setGender(customer.getGender());
        dto.setAvatarUrl(customer.getAvatarUrl());     // THÊM
        dto.setDateOfBirth(customer.getDateOfBirth()); // ✅

        return dto;
    }


}
