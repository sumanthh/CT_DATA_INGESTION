package com.ct.fhir.mapper;

import com.ct.fhir.model.PatientEntity;
import com.ct.fhir.util.AppConstants;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FhirPatientMapper {

    public Map<String, Object> toFhir(PatientEntity entity) {

        Map<String, Object> patient = new LinkedHashMap<>();
        patient.put(AppConstants.RESOURCE_TYPE, AppConstants.PATIENT);
        patient.put(AppConstants.ID, entity.getId());

        patient.put(AppConstants.NAME, List.of(
                Map.of(
                        AppConstants.USE, AppConstants.OFFICIAL,
                        AppConstants.FAMILY, entity.getLastName(),
                        AppConstants.GIVEN, List.of(entity.getFirstName())
                )
        ));

        patient.put(AppConstants.GENDER, entity.getGender());
        patient.put(AppConstants.BIRTHDATE, entity.getBirthDate());

        List<Map<String, Object>> telecom = new ArrayList<>();

        Map<String, Object> phone = new HashMap<>();
        phone.put(AppConstants.SYSTEM, AppConstants.PHONE);
        phone.put(AppConstants.VALUE, entity.getPhone());
        telecom.add(phone);

        if (entity.getEmail() != null) {
            Map<String, Object> email = new HashMap<>();
            email.put(AppConstants.SYSTEM, AppConstants.EMAIL);
            email.put(AppConstants.VALUE, entity.getEmail());
            telecom.add(email);
        }

        patient.put(AppConstants.TELECOM, telecom);

        Map<String, Object> address = new HashMap<>();
        address.put(AppConstants.LINE, List.of(entity.getAddressLine()));
        address.put(AppConstants.CITY, entity.getCity());
        address.put(AppConstants.STATE, entity.getState());

        if (entity.getZip() != null) {
            address.put(AppConstants.POSTALCODE, entity.getZip());
        }

        patient.put(AppConstants.ADDRESS, List.of(address));

        return patient;
    }
}