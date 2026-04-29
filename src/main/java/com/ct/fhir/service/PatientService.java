package com.ct.fhir.service;

import com.ct.fhir.exception.PatientNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * Service interface for patient data operations.
 * 
 * Defines the contract for business logic operations related to patient retrieval.
 * Implementations handle database queries, data transformation, and FHIR formatting.
 */
public interface PatientService {

    /**
     * Retrieves a single patient by ID.
     * 
     * @param id The unique patient identifier
     * @return A FHIR R4 Patient resource as a Map<String, Object>
     * @throws PatientNotFoundException if patient with given ID does not exist
     */
    Map<String, Object> getPatientById(String id);

    /**
     * Retrieves all patients (optionally filtered by source) as a FHIR Bundle.
     * 
     * @param source Optional filter by data source (SOURCE_A or SOURCE_B).
     *               If null or blank, returns all patients from both sources.
     * @return A list containing a single FHIR R4 Bundle resource with all matching patients
     */
    List<Map<String, Object>> getAllBundlePatients(String source);

}
