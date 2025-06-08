package com.example.demoSWP.exception;

import com.example.demoSWP.exception.exceptions.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {

    // Bắt lỗi validate dữ liệu (ví dụ: @NotBlank, @Email,...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBadRequestException(MethodArgumentNotValidException exception){
        System.out.println("Người dùng nhập chưa đúng thông tin");
        StringBuilder responseMessage = new StringBuilder();

        for (FieldError fieldError : exception.getFieldErrors()) {
            responseMessage.append(fieldError.getDefaultMessage()).append("\n");
        }

        return new ResponseEntity<>(responseMessage.toString().trim(), HttpStatus.BAD_REQUEST);
    }

    // Bắt lỗi xác thực tài khoản
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
