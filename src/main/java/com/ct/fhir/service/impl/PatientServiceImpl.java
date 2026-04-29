package com.ct.fhir.service.impl;

import com.ct.fhir.dao.PatientRepository;
import com.ct.fhir.exception.PatientNotFoundException;
import com.ct.fhir.mapper.FhirPatientMapper;
import com.ct.fhir.model.PatientEntity;
import com.ct.fhir.util.AppConstants;
import com.ct.fhir.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation of PatientService interface.
 * 
 * Provides business logic for patient data retrieval operations.
 * Handles database queries via PatientRepository and transforms PatientEntity objects
 * to FHIR R4 format using FhirPatientMapper.
 */
@Service
@Slf4j
public class PatientServiceImpl implements PatientService {
    private final PatientRepository repository;
    private final FhirPatientMapper mapper;

    /**
     * Constructor for dependency injection of repository and mapper.
     * 
     * @param repository The DAO for patient database operations
     * @param mapper The mapper to convert entities to FHIR format
     * @param patientDaoImpl Unused parameter (legacy code)
     */
    public PatientServiceImpl(PatientRepository repository,
                              FhirPatientMapper mapper, PatientRepository patientDaoImpl) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a single patient by ID and converts to FHIR R4 format.
     * 
     * @param id The unique patient identifier
     * @return A FHIR R4 Patient resource as a Map
     * @throws PatientNotFoundException if patient with given ID does not exist
     */
    @Override
    public Map<String, Object> getPatientById(String id) {
        log.debug("Fetching patient id={} ", id);
        PatientEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new PatientNotFoundException(id));

        return mapper.toFhir(entity);
    }

    /**
     * Retrieves all patients (optionally filtered by source) and wraps them in a FHIR Bundle.
     * 
     * Process:
     * 1. Query database for all patients or filter by source
     * 2. Convert each PatientEntity to FHIR R4 format
     * 3. Wrap all patients in a FHIR Bundle resource
     * 4. Return as a single-element list containing the bundle
     * 
     * @param source Optional filter by data source (SOURCE_A or SOURCE_B).
     *               If null or blank, returns all patients.
     * @return A list containing a single FHIR R4 Bundle with all matching patients
     */
    @Override
    public List<Map<String, Object>> getAllBundlePatients(String source) {
        List<PatientEntity> entities =
                (source == null || source.isBlank())
                        ? repository.findAll()
                        : repository.findBySource(source);

        log.info("API fetched {} patients from DB", entities.size());

        // Convert entities to FHIR format
        List<Map<String, Object>> patients = entities.stream()
                .map(mapper::toFhir)
                .toList();

        // Wrap each patient in a Bundle entry
        List<Map<String, Map<String, Object>>> entries = patients.stream()
                .map(p -> Map.of(AppConstants.RESOURCE, p))
                .toList();

        // Create FHIR Bundle resource
        Map<String, Object> bundle = new LinkedHashMap<>();
        bundle.put(AppConstants.RESOURCE_TYPE, AppConstants.BUNDLE);
        bundle.put(AppConstants.TYPE, AppConstants.COLLECTION);
        bundle.put(AppConstants.TOTAL, entries.size());
        bundle.put(AppConstants.ENTRY, entries);
        return Collections.singletonList(bundle);

    }

}



