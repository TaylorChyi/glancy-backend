package com.glancy.backend.exception;

import com.glancy.backend.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.glancy.backend.service.AlertService;

/**
 * Handles application exceptions and logs them.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final AlertService alertService;

    public GlobalExceptionHandler(AlertService alertService) {
        this.alertService = alertService;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Request resulted in error: {}", ex.getMessage(), ex);
        alertService.sendAlert("Illegal argument", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        alertService.sendAlert("Unhandled exception", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("内部服务器错误"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
