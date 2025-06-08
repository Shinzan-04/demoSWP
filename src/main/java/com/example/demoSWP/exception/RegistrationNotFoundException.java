// src/main/java/com/example/demoSWP/exception/RegistrationNotFoundException.java
package com.example.demoSWP.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Sets the HTTP status code to 404 Not Found when this exception is thrown
public class RegistrationNotFoundException extends RuntimeException {

    public RegistrationNotFoundException(String message) {
        super(message);
    }

    public RegistrationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}