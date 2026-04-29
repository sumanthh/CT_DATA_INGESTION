package com.ct.fhir.util;

/**
 * Application-wide constants for field names, FHIR resource types, and data source identifiers.
 * 
 * Centralizes all hardcoded string values to:
 * - Prevent typos and inconsistencies
 * - Enable easy refactoring
 * - Improve code maintainability
 * - Provide a single source of truth for field mappings
 * 
 * Constants are organized by category:
 * - Source A field names (patient_id, first_name, etc.)
 * - Source B field names (id, full_name, etc.)
 * - Source identifiers (SOURCE_A, SOURCE_B)
 * - FHIR R4 resource types and field names
 * - Bundle-related constants
 */
public class AppConstants {
    
    // ===== Source A Field Names =====
    public static final String PATIENT_ID = "patient_id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String DOB = "dob";
    public static final String GENDER = "gender";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String STREET = "street";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String ZIP = "zip";

    // ===== Source B Field Names =====
    public static final String ID = "id";
    public static final String FULL_NAME = "full_name";
    public static final String BIRTH_DATE = "birth_date";
    public static final String SEX = "sex";
    public static final String CONTACT_NUMBER = "contact_number";
    public static final String ADDR_LINE = "addr_line";
    public static final String ADDR_CITY = "addr_city";
    public static final String ADDR_STATE = "addr_state";

    // ===== Data Source Identifiers =====
    public static final String SOURCE_A = "SOURCE_A";
    public static final String SOURCE_B = "SOURCE_B";

    // ===== FHIR R4 Resource Types =====
    public static final String RESOURCE_TYPE = "resourceType";
    public static final String PATIENT = "Patient";
    public static final String BUNDLE = "Bundle";

    // ===== FHIR R4 Patient Fields =====
    public static final String NAME = "name";
    public static final String USE = "use";
    public static final String OFFICIAL = "official";
    public static final String FAMILY = "family";
    public static final String GIVEN = "given";
    public static final String BIRTHDATE = "birthDate";
    public static final String SYSTEM = "system";
    public static final String VALUE = "value";
    public static final String TELECOM = "telecom";
    public static final String LINE = "line";
    public static final String POSTALCODE = "postalCode";
    public static final String ADDRESS = "address";

    // ===== FHIR R4 Bundle Fields =====
    public static final String COLLECTION = "collection";
    public static final String TYPE = "type";
    public static final String TOTAL = "total";
    public static final String ENTRY = "entry";
    public static final String RESOURCE = "resource";
}
