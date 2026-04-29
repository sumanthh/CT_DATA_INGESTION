package com.ct.fhir.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler for REST API endpoints.
 * 
 * Centralizes exception handling across all controllers.
 * Converts application exceptions to standardized HTTP responses with FHIR OperationOutcome format.
 * 
 * This ensures consistent error responses and proper HTTP status codes.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles PatientNotFoundException and returns HTTP 404 with FHIR OperationOutcome.
     * 
     * When a patient is not found, this method:
     * 1. Logs a warning message
     * 2. Creates a FHIR OperationOutcome resource
     * 3. Returns HTTP 404 (Not Found) status
     * 
     * @param ex The PatientNotFoundException thrown by the service
     * @return ResponseEntity with HTTP 404 and FHIR OperationOutcome body
     */
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePatientNotFound(
            PatientNotFoundException ex) {
        log.warn("Patient not found: {}", ex.getMessage());

        Map<String, Object> operationOutcome = new LinkedHashMap<>();
        operationOutcome.put("resourceType", "OperationOutcome");
        operationOutcome.put("issue", List.of(
                Map.of(
                        "severity", "error",
                        "code", "not-found",
                        "diagnostics", ex.getMessage()
                )
        ));
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(operationOutcome);
    }
}