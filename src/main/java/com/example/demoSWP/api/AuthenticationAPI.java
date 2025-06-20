package com.example.demoSWP.api;

import com.example.demoSWP.dto.AccountResponse;
import com.example.demoSWP.dto.ForgotPasswordRequest;
import com.example.demoSWP.dto.LoginRequest;
import com.example.demoSWP.dto.ResetPasswordRequest;
import com.example.demoSWP.entity.Account;
import com.example.demoSWP.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Indicates that this class is a REST controller
@RequestMapping("/api") // Maps all requests to /api
@CrossOrigin(origins = "http://localhost:3000") // Allows cross-origin requests from http://localhost:3000
public class AuthenticationAPI {

    @Autowired // Injects an instance of AuthenticationService
    AuthenticationService authenticationService;

    @PostMapping("/register") // Maps POST requests to /api/register
    public ResponseEntity<?> register(@RequestBody Account account) {
        // @RequestBody maps the JSON request body to an Account object
        if (authenticationService.emailExists(account.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã được sử dụng.");
        } else {
            if (authenticationService.phoneExists(account.getPhone())){
                return ResponseEntity.badRequest().body("Phone đã được sử dụng");
            }
        }
        Account newAccount = authenticationService.register(account); // Calls the service to register the account
        return ResponseEntity.ok(newAccount); // Returns a 200 OK response with the new account
    }

    @PostMapping("/login") // Maps POST requests to /api/login
    public ResponseEntity<AccountResponse> login(@RequestBody LoginRequest loginRequest) {
        // @RequestBody maps the JSON request body to a LoginRequest object
        AccountResponse account = authenticationService.login(loginRequest); // Calls the service to handle login
        return ResponseEntity.ok(account); // Returns a 200 OK response with the AccountResponse (likely containing a token)
    }

    @PostMapping("/forgot-pasword")
    public ResponseEntity fogotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest){
        authenticationService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok("successfully forgotPassword");
    }

    @SecurityRequirement(name = "api")
    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest){
        authenticationService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok("successfully resetPassword");
    }
}