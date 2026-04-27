package com.ct.fhir.controller;

import com.ct.fhir.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@Slf4j
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @GetMapping
    public List<Map<String, Object>> getPatients(
            @RequestParam(required = false) String source) {
        log.info("GET /api/patients called with source={} ", source);
        return service.getAllPatients(source);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getPatient(@PathVariable String id) {
        log.info("GET /api/patients/{} called with id: ", id);
        return service.getPatientById(id);
    }
}