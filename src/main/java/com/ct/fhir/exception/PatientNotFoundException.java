package com.ct.fhir.exception;

/**
 * Custom exception thrown when a patient record is not found in the database.
 * 
 * This exception is caught by GlobalExceptionHandler and converted to an HTTP 404 response
 * with a FHIR OperationOutcome resource.
 */
public class PatientNotFoundException extends RuntimeException {
    
    /**
     * Constructs a PatientNotFoundException with a descriptive message.
     * 
     * @param id The patient ID that was not found
     */
    public PatientNotFoundException(String id) {
        super("Patient with id " + id + " not found");
    }
}

