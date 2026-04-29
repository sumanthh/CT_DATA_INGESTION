package com.ct.ingestion.facade;

import com.ct.fhir.dao.PatientRepository;
import com.ct.fhir.model.PatientEntity;
import com.ct.ingestion.mapper.PatientNormalizer;
import com.ct.ingestion.parser.SourceAPatientParser;
import com.ct.ingestion.parser.SourceBPatientParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Facade component that orchestrates the patient data ingestion process.
 * 
 * Coordinates the entire ingestion workflow:
 * 1. Parses patient data from Source A (CSV) and Source B (TSV) files
 * 2. Normalizes heterogeneous data into a unified schema
 * 3. Persists data to H2 database with idempotency checks
 * 
 * The ingestion is fully idempotent - running it multiple times does not create duplicates.
 * Existing patients are skipped based on their unique ID.
 */
@Component
@Slf4j
public class PatientIngestionFacade {

    private final PatientRepository repository;
    private final SourceAPatientParser sourceAParser;
    private final SourceBPatientParser sourceBParser;
    private final PatientNormalizer normalizer;

    /**
     * Constructor for dependency injection of all required components.
     * 
     * @param repository The DAO for database operations
     * @param sourceAParser Parser for Source A CSV files
     * @param sourceBParser Parser for Source B TSV files
     * @param normalizer Normalizer to convert raw data to unified schema
     */
    public PatientIngestionFacade(PatientRepository repository, SourceAPatientParser sourceAParser, SourceBPatientParser sourceBParser, PatientNormalizer normalizer) {
        this.repository = repository;
        this.sourceAParser = sourceAParser;
        this.sourceBParser = sourceBParser;
        this.normalizer = normalizer;
    }

    /**
     * Orchestrates the complete ingestion process for both data sources.
     * 
     * Process:
     * 1. Logs ingestion start
     * 2. Ingests Source A patients (CSV)
     * 3. Ingests Source B patients (TSV)
     * 4. Logs total records in database
     * 
     * This method is transactional - if any operation fails, all changes are rolled back.
     * Ensures data consistency and atomicity.
     */
    @Transactional
    public void ingestAll() {
        log.info("Patient ingestion STARTED");
        ingestSourceA();
        ingestSourceB();
        log.info("Patient ingestion COMPLETED. Total records in DB: {}",
                repository.count());
    }

    /**
     * Ingests patient data from Source A (CSV file).
     * 
     * Process:
     * 1. Parses CSV file to extract raw patient records
     * 2. Normalizes each record using PatientNormalizer.fromSourceA()
     * 3. Saves only new patients (skips existing ones for idempotency)
     */
    private void ingestSourceA() {
        List<Map<String, String>> rawRecords = sourceAParser.parse();
        log.info("Source A records read = {}", rawRecords.size());

        rawRecords.stream()
                .map(normalizer::fromSourceA)
                .forEach(this::saveIfNotExists);
    }

    /**
     * Ingests patient data from Source B (TSV file).
     * 
     * Process:
     * 1. Parses TSV file to extract raw patient records
     * 2. Normalizes each record using PatientNormalizer.fromSourceB()
     * 3. Saves only new patients (skips existing ones for idempotency)
     */
    private void ingestSourceB() {
        List<Map<String, String>> rawRecords = sourceBParser.parse();
        log.info("Source B records read = {}", rawRecords.size());

        rawRecords.stream()
                .map(normalizer::fromSourceB)
                .forEach(this::saveIfNotExists);
    }

    /**
     * Saves a patient to the database only if it doesn't already exist.
     * 
     * Implements idempotency by checking if patient ID exists before saving.
     * If patient exists, it is skipped (not updated).
     * 
     * @param patient The normalized PatientEntity to save
     */
    private void saveIfNotExists(PatientEntity patient) {
        if (repository.existsById(patient.getId())) {
            log.debug("Skipping existing patient id={}", patient.getId());
        } else {
            repository.save(patient);
            log.debug("Inserted patient id={}", patient.getId());
        }

    }

}

