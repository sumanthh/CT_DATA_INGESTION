package com.ct.fhir.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePatientNotFound(
            PatientNotFoundException ex) {
        log.warn("Patient not found: {}", ex.getMessage());
        Map<String, Object> operationOutcome = Map.of(
                "resourceType", "OperationOutcome",
                "issue", List.of(
                        Map.of(
                                "severity", "error",
                                "code", "not-found",
                                "diagnostics", ex.getMessage()
                        )
                )
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(operationOutcome);
    }
}