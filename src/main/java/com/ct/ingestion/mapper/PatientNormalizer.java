package com.ct.ingestion.mapper;

import com.ct.fhir.model.PatientEntity;
import com.ct.fhir.util.AppConstants;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Normalizer component that converts raw patient data from different sources into a unified schema.
 * 
 * Handles data transformation for two heterogeneous sources:
 * - Source A: CSV with pre-split fields and DD-MM-YYYY date format
 * - Source B: TSV with full names and DD/MM/YYYY date format
 * 
 * Normalization includes:
 * - Name splitting (Source B only)
 * - Date format conversion (Source A: DD-MM-YYYY → YYYY-MM-DD, Source B: DD/MM/YYYY → YYYY-MM-DD)
 * - Gender mapping (M/F/MALE/FEMALE → male/female/unknown)
 * - Field mapping to unified schema
 */
@Component
public class PatientNormalizer {

    private static final DateTimeFormatter SOURCE_A_DATE =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter SOURCE_B_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Converts a raw Source A record to a normalized PatientEntity.
     * 
     * Source A format:
     * - Fields are already split (first_name, last_name)
     * - Date is in DD-MM-YYYY format and must be converted to YYYY-MM-DD
     * - Gender is single character (M/F) or full word (MALE/FEMALE)
     * - Email and zip are included
     * 
     * @param raw A map containing raw patient data from Source A
     * @return A normalized PatientEntity ready for database persistence
     */
    public PatientEntity fromSourceA(Map<String, String> raw) {

        PatientEntity p = new PatientEntity();
        p.setId(raw.get(AppConstants.PATIENT_ID));
        p.setSource(AppConstants.SOURCE_A);
        p.setFirstName(raw.get(AppConstants.FIRST_NAME));
        p.setLastName(raw.get(AppConstants.LAST_NAME));
        // Convert date from DD-MM-YYYY to YYYY-MM-DD
        p.setBirthDate(
                LocalDate.parse(raw.get(AppConstants.DOB), SOURCE_A_DATE).toString()
        );
        p.setGender(normalizeGender(raw.get(AppConstants.GENDER)));
        p.setPhone(raw.get(AppConstants.PHONE));
        p.setEmail(raw.get(AppConstants.EMAIL));
        p.setAddressLine(raw.get(AppConstants.STREET));
        p.setCity(raw.get(AppConstants.CITY));
        p.setState(raw.get(AppConstants.STATE));
        p.setZip(raw.get(AppConstants.ZIP));

        return p;
    }

    /**
     * Converts a raw Source B record to a normalized PatientEntity.
     * 
     * Source B format:
     * - Full name needs to be split into first and last names
     * - Date is in DD/MM/YYYY format and must be converted to YYYY-MM-DD
     * - Gender is single character (M/F)
     * - Email is always set to null (not provided in Source B)
     * - Zip is always set to null (not provided in Source B)
     * 
     * @param raw A map containing raw patient data from Source B
     * @return A normalized PatientEntity ready for database persistence
     */
    public PatientEntity fromSourceB(Map<String, String> raw) {

        // Split full name into first and last names
        String[] nameParts = raw.get(AppConstants.FULL_NAME).split(" ", 2);

        PatientEntity p = new PatientEntity();
        p.setId(raw.get(AppConstants.ID));
        p.setSource(AppConstants.SOURCE_B);
        p.setFirstName(nameParts[0]);
        p.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        // Convert date from DD/MM/YYYY to YYYY-MM-DD
        p.setBirthDate(
                LocalDate.parse(raw.get(AppConstants.BIRTH_DATE), SOURCE_B_DATE).toString()
        );
        p.setGender(normalizeGender(raw.get(AppConstants.SEX)));
        p.setPhone(raw.get(AppConstants.CONTACT_NUMBER));
        p.setEmail(null); // Source B does not provide email
        p.setAddressLine(raw.get(AppConstants.ADDR_LINE));
        p.setCity(raw.get(AppConstants.ADDR_CITY));
        p.setState(raw.get(AppConstants.ADDR_STATE));
        p.setZip(null); // Source B does not provide zip

        return p;
    }

    /**
     * Normalizes gender values to a standard format.
     * 
     * Mapping:
     * - M, MALE → male
     * - F, FEMALE → female
     * - null or any other value → unknown
     * 
     * @param value The raw gender value from source data
     * @return Normalized gender value (male, female, or unknown)
     */
    private String normalizeGender(String value) {
        if (value == null) return "unknown";

        return switch (value.trim().toUpperCase()) {
            case "M", "MALE" -> "male";
            case "F", "FEMALE" -> "female";
            default -> "unknown";
        };
    }
}
