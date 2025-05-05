package com.tzl.booking.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tzl.booking.utils.CustomApiResponse;
import com.tzl.booking.utils.ResponseConstants;

import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        logger.error("Unhandled exception occurred", e);

        CustomApiResponse<String> response = new CustomApiResponse<>(
                ResponseConstants.ERROR_CODE,
                ResponseConstants.ERROR_MESSAGE,
                e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        logger.error("Bad credentials exception", e);

        CustomApiResponse<String> response = new CustomApiResponse<>(
                ResponseConstants.UNAUTHORIZED_ERROR_CODE,
                ResponseConstants.UNAUTHORIZED_ERROR_MESSAGE,
                e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Handle database constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logger.error("Data integrity violation", e);

        CustomApiResponse<String> response = new CustomApiResponse<>(
                ResponseConstants.BAD_REQUEST_ERROR_CODE,
                ResponseConstants.BAD_REQUEST_ERROR_MESSAGE,
                // e.getMessage(),
                "");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        logger.error("Validation error: {}", errorMessage, e);

        CustomApiResponse<String> response = new CustomApiResponse<>(
                ResponseConstants.BAD_REQUEST_ERROR_CODE,
                // ResponseConstants.BAD_REQUEST_ERROR_MESSAGE,
                errorMessage,
                "");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        logger.error("Bind exception: {}", errorMessage, e);
        CustomApiResponse<String> response = new CustomApiResponse<>(
                ResponseConstants.BAD_REQUEST_ERROR_CODE,
                ResponseConstants.BINDING_ERROR_MESSAGE,
                errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomApiResponse<String>> handleResourceNotFound(ResourceNotFoundException e) {
        logger.error("Resource not found", e);
        CustomApiResponse<String> response = new CustomApiResponse<>(
                ResponseConstants.NOT_FOUND_CODE,
                ResponseConstants.NOT_FOUND_MESSAGE,
                e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        CustomApiResponse<String> response = CustomApiResponse.<String>builder()
                .returnCode(ResponseConstants.BAD_REQUEST_ERROR_CODE)
                .returnMessage(ResponseConstants.BAD_REQUEST_ERROR_MESSAGE)
                .data(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<CustomApiResponse<String>> handleBusinessRuleException(BusinessRuleException ex) {
        CustomApiResponse<String> response = CustomApiResponse.<String>builder()
                .returnCode(ResponseConstants.BAD_REQUEST_ERROR_CODE)
                .returnMessage(ResponseConstants.BAD_REQUEST_ERROR_MESSAGE)
                .data(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}