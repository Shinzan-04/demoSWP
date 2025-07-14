package com.example.demoSWP.exception;

import com.example.demoSWP.exception.exceptions.AuthenticationException;
import com.example.demoSWP.exception.exceptions.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBadRequestException(MethodArgumentNotValidException exception){
        System.out.println("Người dùng nhập chưa đúng thông tin");
        StringBuilder responseMessage = new StringBuilder();

        for (FieldError fieldError : exception.getFieldErrors()) {
            responseMessage.append(fieldError.getDefaultMessage()).append("\n");
        }

        return new ResponseEntity<>(responseMessage.toString().trim(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // Bắt lỗi BadRequestException trả về 400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Bắt lỗi NotFoundException (ví dụ RegistrationNotFoundException, ResourceNotFoundException) trả về 404
    @ExceptionHandler({
            RegistrationNotFoundException.class,
            ResourceNotFoundException.class
    })
    public ResponseEntity<?> handleNotFound(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Bắt lỗi chung, trả về 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllException(Exception ex) {
        ex.printStackTrace(); // log chi tiết lỗi ra console
        return new ResponseEntity<>("Lỗi server: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

