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

@Service
@Slf4j
public class PatientServiceImpl implements PatientService {
    private final PatientRepository repository;
    private final FhirPatientMapper mapper;

    public PatientServiceImpl(PatientRepository repository,
                              FhirPatientMapper mapper, PatientRepository patientDaoImpl) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /*@Override
    public List<Map<String, Object>> getAllPatients(String source) {

        List<PatientEntity> entities =
                (source == null || source.isBlank())
                        ? repository.findAll()
                        : repository.findBySource(source);

        log.info("API fetched {} patients from DB ", entities.size());
        return entities.stream()
                .map(mapper::toFhir)
                .toList();
    }*/

    @Override
    public Map<String, Object> getPatientById(String id) {
        log.debug("Fetching patient id={} ", id);
        PatientEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new PatientNotFoundException(id));

        return mapper.toFhir(entity);
    }

    @Override
    public List<Map<String, Object>> getAllBundlePatients(String source) {
        List<PatientEntity> entities =
                (source == null || source.isBlank())
                        ? repository.findAll()
                        : repository.findBySource(source);

        log.info("API fetched {} patients from DB", entities.size());

        List<Map<String, Object>> patients = entities.stream()
                .map(mapper::toFhir)
                .toList();

        List<Map<String, Map<String, Object>>> entries = patients.stream()
                .map(p -> Map.of(AppConstants.RESOURCE, p))
                .toList();

        Map<String, Object> bundle = new LinkedHashMap<>();
        bundle.put(AppConstants.RESOURCE_TYPE, AppConstants.BUNDLE);
        bundle.put(AppConstants.TYPE, AppConstants.COLLECTION);
        bundle.put(AppConstants.TOTAL, entries.size());
        bundle.put(AppConstants.ENTRY, entries);
        return Collections.singletonList(bundle);

    }

    }



