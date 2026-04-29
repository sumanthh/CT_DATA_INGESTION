package com.ct.fhir.controller;

import com.ct.fhir.exception.PatientNotFoundException;
import com.ct.fhir.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API controller for patient data retrieval.
 * 
 * Provides HTTP endpoints to fetch patient records in FHIR R4 format.
 * All endpoints are prefixed with /api/patients.
 * 
 * Security: User inputs are sanitized before logging to prevent log injection attacks.
 */
@RestController
@RequestMapping("/api/patients")
@Slf4j
public class PatientController {

    private final PatientService service;

    /**
     * Constructor for dependency injection of PatientService.
     * 
     * @param service The service layer for patient operations
     */
    public PatientController(PatientService service) {
        this.service = service;
    }

    /**
     * Retrieves a single patient by ID in FHIR R4 format.
     * 
     * HTTP Method: GET
     * Endpoint: /api/patients/{id}
     * 
     * @param id The unique patient identifier (e.g., PA001, PB001)
     * @return A FHIR R4 Patient resource as a Map
     * @throws PatientNotFoundException if patient with given ID does not exist (HTTP 404)
     * 
     */
    @GetMapping("/{id}")
    public Map<String, Object> getPatient(@PathVariable String id) {
        log.info("GET /api/patients/{} called with id: ", sanitize(id));
        return service.getPatientById(id);
    }

    /**
     * Retrieves all patients (optionally filtered by source) as a FHIR Bundle.
     * 
     * HTTP Method: GET
     * Endpoint: /api/patients
     * 
     * @param source Optional query parameter to filter by data source (SOURCE_A or SOURCE_B).
     *               If not provided, returns all patients from both sources.
     * @return A FHIR R4 Bundle containing patient resources
     * 
     */
    @GetMapping
    public List<Map<String, Object>> getBundlePatients(
            @RequestParam(required = false) String source) {
        log.info("GET /api/patients called with source={} ", sanitize(source));
        return service.getAllBundlePatients(source);
    }

    /**
     * Sanitizes user input by removing CRLF characters to prevent log injection attacks.
     * 
     * Replaces newline (\n) and carriage return (\r) characters with underscores.
     * This prevents attackers from forging log entries or breaking log integrity.
     * 
     * @param value The input string to sanitize
     * @return Sanitized string with CRLF characters replaced, or null if input is null
     */
    private String sanitize(String value) {
        return value == null ? null : value.replaceAll("[\n\r]", "_");
    }
}