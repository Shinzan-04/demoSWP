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
    private String dateOfBirth; // format as ISO string or LocalDate
    private String avatarUrl;

    public CustomerDTO(String fullName, String phone, String email) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }


    public static Customer toEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setCustomerID(dto.getCustomerID());
        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());
        customer.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth())); // CHUYỂN
        return customer;
    }


}
