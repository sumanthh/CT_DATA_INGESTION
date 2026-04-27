package com.ct.fhir.service;

import java.util.List;
import java.util.Map;

public interface PatientService {

    List<Map<String, Object>> getAllPatients(String source);

    Map<String, Object> getPatientById(String id);
}
