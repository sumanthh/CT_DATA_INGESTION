package com.ct.fhir.service;

import java.util.List;
import java.util.Map;

public interface PatientService {

    Map<String, Object> getPatientById(String id);

    List<Map<String, Object>> getAllBundlePatients(String source);

}
