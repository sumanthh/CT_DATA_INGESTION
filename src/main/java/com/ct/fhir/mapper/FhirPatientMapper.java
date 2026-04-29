package com.ct.fhir.mapper;

import com.ct.fhir.model.PatientEntity;
import com.ct.fhir.util.AppConstants;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Mapper component that converts PatientEntity objects to FHIR R4 Patient resources.
 * 
 * Transforms internal database entities into standardized FHIR (Fast Healthcare Interoperability Resources)
 * JSON format for API responses. Handles optional fields and constructs proper FHIR structures.
 */
@Component
public class FhirPatientMapper {

    /**
     * Converts a PatientEntity to a FHIR R4 Patient resource.
     * 
     * Process:
     * 1. Creates a LinkedHashMap to maintain field order
     * 2. Sets resourceType and ID
     * 3. Constructs name array with family and given names
     * 4. Sets gender and birthDate
     * 5. Builds telecom array with phone and optional email
     * 6. Constructs address with line, city, state, and optional postal code
     * 
     * @param entity The PatientEntity from the database
     * @return A Map representing a FHIR R4 Patient resource
     * 
     */
    public Map<String, Object> toFhir(PatientEntity entity) {

        Map<String, Object> patient = new LinkedHashMap<>();
        patient.put(AppConstants.RESOURCE_TYPE, AppConstants.PATIENT);
        patient.put(AppConstants.ID, entity.getId());

        // Build name structure
        patient.put(AppConstants.NAME, List.of(
                Map.of(
                        AppConstants.USE, AppConstants.OFFICIAL,
                        AppConstants.FAMILY, entity.getLastName(),
                        AppConstants.GIVEN, List.of(entity.getFirstName())
                )
        ));

        patient.put(AppConstants.GENDER, entity.getGender());
        patient.put(AppConstants.BIRTHDATE, entity.getBirthDate());

        // Build telecom (contact) array
        List<Map<String, Object>> telecom = new ArrayList<>();

        Map<String, Object> phone = new HashMap<>();
        phone.put(AppConstants.SYSTEM, AppConstants.PHONE);
        phone.put(AppConstants.VALUE, entity.getPhone());
        telecom.add(phone);

        // Add email only if present
        if (entity.getEmail() != null) {
            Map<String, Object> email = new HashMap<>();
            email.put(AppConstants.SYSTEM, AppConstants.EMAIL);
            email.put(AppConstants.VALUE, entity.getEmail());
            telecom.add(email);
        }

        patient.put(AppConstants.TELECOM, telecom);

        // Build address structure
        Map<String, Object> address = new HashMap<>();
        address.put(AppConstants.LINE, List.of(entity.getAddressLine()));
        address.put(AppConstants.CITY, entity.getCity());
        address.put(AppConstants.STATE, entity.getState());

        // Add postal code only if present
        if (entity.getZip() != null) {
            address.put(AppConstants.POSTALCODE, entity.getZip());
        }

        patient.put(AppConstants.ADDRESS, List.of(address));

        return patient;
    }
}