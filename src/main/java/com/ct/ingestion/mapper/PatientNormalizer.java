package com.ct.ingestion.mapper;

import com.ct.fhir.model.PatientEntity;
import com.ct.fhir.util.AppConstants;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class PatientNormalizer {

    private static final DateTimeFormatter SOURCE_B_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PatientEntity fromSourceA(Map<String, String> raw) {

        PatientEntity p = new PatientEntity();
        p.setId(raw.get(AppConstants.PATIENT_ID));
        p.setSource(AppConstants.SOURCE_A);
        p.setFirstName(raw.get(AppConstants.FIRST_NAME));
        p.setLastName(raw.get(AppConstants.LAST_NAME));
        p.setBirthDate(raw.get(AppConstants.DOB)); // already YYYY-MM-DD
        p.setGender(normalizeGender(raw.get(AppConstants.GENDER)));
        p.setPhone(raw.get(AppConstants.PHONE));
        p.setEmail(raw.get(AppConstants.EMAIL));
        p.setAddressLine(raw.get(AppConstants.STREET));
        p.setCity(raw.get(AppConstants.CITY));
        p.setState(raw.get(AppConstants.STATE));
        p.setZip(raw.get(AppConstants.ZIP));

        return p;
    }

    public PatientEntity fromSourceB(Map<String, String> raw) {

        String[] nameParts = raw.get(AppConstants.FULL_NAME).split(" ", 2);

        PatientEntity p = new PatientEntity();
        p.setId(raw.get(AppConstants.ID));
        p.setSource(AppConstants.SOURCE_B);
        p.setFirstName(nameParts[0]);
        p.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        p.setBirthDate(
                LocalDate.parse(raw.get(AppConstants.BIRTH_DATE), SOURCE_B_DATE).toString()
        );
        p.setGender(normalizeGender(raw.get(AppConstants.SEX)));
        p.setPhone(raw.get(AppConstants.CONTACT_NUMBER));
        p.setEmail(null);
        p.setAddressLine(raw.get(AppConstants.ADDR_LINE));
        p.setCity(raw.get(AppConstants.ADDR_CITY));
        p.setState(raw.get(AppConstants.STATE));
        p.setZip(null);

        return p;
    }

    private String normalizeGender(String value) {
        if (value == null) return "unknown";

        return switch (value.trim().toUpperCase()) {
            case "M", "MALE" -> "male";
            case "F", "FEMALE" -> "female";
            default -> "unknown";
        };
    }
}
