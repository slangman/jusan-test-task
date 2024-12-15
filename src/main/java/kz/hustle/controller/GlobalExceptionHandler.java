package kz.hustle.controller;

import jakarta.validation.ConstraintViolationException;
import kz.hustle.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.net.ConnectException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(WeatherAPIForbiddenException.class)
    public ResponseEntity<String> handleWeatherAPIForbiddenException(WeatherAPIForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(WeatherAPIRequestTimeoutException.class)
    public ResponseEntity<String> handleWeatherAPIRequestTimeoutException(WeatherAPIRequestTimeoutException ex) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(ex.getMessage());
    }

    @ExceptionHandler(WeatherAPIUnauthorizedException.class)
    public ResponseEntity<String> handleWeatherAPIUnauthorizedException(WeatherAPIUnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<String> handleConnectException(ConnectException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Weather API is unavailable. Check your network connection.");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleGeneralException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationExceptions(ConstraintViolationException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getConstraintViolations().forEach(violation -> errors.append(violation.getMessage()).append("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
    }

    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ResponseEntity<String> handleValidationErrors(BindingResult result) {
        StringBuilder errors = new StringBuilder();
        for (FieldError error : result.getFieldErrors()) {
            errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
    }

    @ExceptionHandler(CountryAlreadyExistsException.class)
    public ResponseEntity<String> handleCountryAlreadyExistsException(CountryAlreadyExistsException ex) {
        StringBuilder errors = new StringBuilder();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RegionAlreadyExistsException.class)
    public ResponseEntity<String> handleRegionAlreadyExistsException(RegionAlreadyExistsException ex) {
        StringBuilder errors = new StringBuilder();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
