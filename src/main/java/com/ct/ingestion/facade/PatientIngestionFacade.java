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

@Component
@Slf4j
public class PatientIngestionFacade {

    private final PatientRepository repository;
    private final SourceAPatientParser sourceAParser;
    private final SourceBPatientParser sourceBParser;
    private final PatientNormalizer normalizer;

    public PatientIngestionFacade(PatientRepository repository, SourceAPatientParser sourceAParser, SourceBPatientParser sourceBParser, PatientNormalizer normalizer) {
        this.repository = repository;
        this.sourceAParser = sourceAParser;
        this.sourceBParser = sourceBParser;
        this.normalizer = normalizer;
    }

    @Transactional
    public void ingestAll() {
        log.info("Patient ingestion STARTED");
        ingestSourceA();
        ingestSourceB();
        log.info("Patient ingestion COMPLETED. Total records in DB: {}",
                repository.count());
    }

    private void ingestSourceA() {
        List<Map<String, String>> rawRecords = sourceAParser.parse();
        log.info("Source A records read = {}", rawRecords.size());

        rawRecords.stream()
                .map(normalizer::fromSourceA)
                .forEach(this::saveIfNotExists);
    }

    private void ingestSourceB() {
        List<Map<String, String>> rawRecords = sourceBParser.parse();
        log.info("Source B records read = {}", rawRecords.size());

        rawRecords.stream()
                .map(normalizer::fromSourceB)
                .forEach(this::saveIfNotExists);
    }

    private void saveIfNotExists(PatientEntity patient) {
        if (repository.existsById(patient.getId())) {
            log.debug("Skipping existing patient id={}", patient.getId());
        } else {
            repository.save(patient);
            log.debug("Inserted patient id={}", patient.getId());
        }

    }

}

