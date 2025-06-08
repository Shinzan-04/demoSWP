package com.example.demoSWP.service;

import com.example.demoSWP.dto.AccountResponse;
import com.example.demoSWP.dto.EmailDetail;
import com.example.demoSWP.dto.LoginRequest;
import com.example.demoSWP.entity.Account;
import com.example.demoSWP.entity.Customer;
import com.example.demoSWP.entity.Doctor;
import com.example.demoSWP.entity.Staff;
import com.example.demoSWP.enums.Role;
import com.example.demoSWP.exception.exceptions.AuthenticationException;
import com.example.demoSWP.repository.AuthenticationRepository;
import com.example.demoSWP.repository.CustomerRepository;
import com.example.demoSWP.repository.DoctorRepository;
import com.example.demoSWP.repository.StaffRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    EmailService emailService;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TokenService tokenService;

    public Account register(Account account){
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Account newAccount = authenticationRepository.save(account);

        // Kiểm tra vai trò của Account và tạo entity tương ứng
        if (newAccount != null && newAccount.getRole() != null) {
            if (newAccount.getRole() == Role.DOCTOR) {
                Doctor doctor = new Doctor();
                doctor.setAccount(newAccount);
                doctor.setEmail(newAccount.getEmail());
                doctor.setFullName(newAccount.getFullName()); // Sử dụng getFullName()
                doctor.setPhone(newAccount.getPhone());
                doctorRepository.save(doctor);
            } else if (newAccount.getRole() == Role.USER) { // Xử lý role USER
                Customer customer = new Customer();
                customer.setAccount(newAccount);
                customer.setGender(newAccount.getGender());
                customer.setEmail(newAccount.getEmail());
                customer.setFullName(newAccount.getFullName()); // Sử dụng getFullName()
                customer.setPhone(newAccount.getPhone());
                customerRepository.save(customer);
            } else if (newAccount.getRole() == Role.STAFF) { // Xử lý role STAFF
                Staff staff = new Staff();
                staff.setAccount(newAccount);
                staff.setEmail(newAccount.getEmail());
                staff.setFullName(newAccount.getFullName()); // Sử dụng getFullName()
                staff.setPhone(newAccount.getPhone());
                staffRepository.save(staff);
            }
        }
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(account.email);
        emailDetail.setSubject("Welcome to my system");
        emailService.sendEmail(emailDetail);
        return newAccount;
    }

    public AccountResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));
        } catch (Exception e) {
            System.out.println("Thông tin đăng nhập không chính xác");

            throw new AuthenticationException("Invalid  username or password");
        }

        Account account = authenticationRepository.findAccountByEmail(loginRequest.getEmail());
        AccountResponse accountResponse = modelMapper.map(account, AccountResponse.class);
        String token = tokenService.generateToken(account);
        accountResponse.setToken(token);
        return accountResponse;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return authenticationRepository.findAccountByEmail(email);
    }
    public Doctor getCurrentDoctor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return doctorRepository.findByAccount_Email(email)
                .orElseThrow(() -> new UsernameNotFoundException("Doctor not found"));
    }

    // check mail
    public boolean emailExists(String email) {
        return authenticationRepository.existsByEmail(email);
    }
    // check phone
    public boolean phoneExists(String phone){
        return authenticationRepository.existsByPhone(phone);
    }
    // THÊM PHƯƠNG THỨC NÀY ĐỂ LẤY customerId TỪ TOKEN/SECURITY CONTEXT
    public Long getCurrentCustomerId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("DEBUG: Searching for Customer with email: " + email); // Debug step 1

        Optional<Customer> customerOptional = customerRepository.findByEmail(email);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            System.out.println("DEBUG: Found Customer for email '" + email + "'. Customer ID: " + customer.getCustomerID()); // Debug step 2
            return customer.getCustomerID();
        } else {
            System.out.println("DEBUG: No Customer found for email: " + email); // Debug step 3
            throw new RuntimeException("Customer not found for authenticated email: " + email);
        }
    }

}