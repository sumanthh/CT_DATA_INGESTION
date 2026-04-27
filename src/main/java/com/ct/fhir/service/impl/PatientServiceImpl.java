package com.ct.fhir.service.impl;

import com.ct.fhir.dao.PatientRepository;
import com.ct.fhir.exception.PatientNotFoundException;
import com.ct.fhir.mapper.FhirPatientMapper;
import com.ct.fhir.model.PatientEntity;

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

    @Autowired
    private final PatientRepository patientDaoImpl;

    public PatientServiceImpl(PatientRepository repository,
                              FhirPatientMapper mapper, PatientRepository patientDaoImpl) {
        this.repository = repository;
        this.mapper = mapper;
        this.patientDaoImpl = patientDaoImpl;
    }

    @Override
    public List<Map<String, Object>> getAllPatients(String source) {

        List<PatientEntity> entities =
                (source == null || source.isBlank())
                        ? repository.findAll()
                        : repository.findBySource(source);

        log.info("API fetched {} patients from DB ", entities.size());
        return entities.stream()
                .map(mapper::toFhir)
                .toList();
    }

    @Override
    public Map<String, Object> getPatientById(String id) {
        log.debug("Fetching patient id={} ", id);
        PatientEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new PatientNotFoundException(id));

        return mapper.toFhir(entity);
    }

    }



