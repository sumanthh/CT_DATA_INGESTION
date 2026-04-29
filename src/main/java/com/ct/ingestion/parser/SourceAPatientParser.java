package com.ct.ingestion.parser;

import com.ct.fhir.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Parser component for Source A patient data (CSV format).
 * 
 * Reads and parses the source_a_patients.csv file from the classpath.
 * Extracts patient records with pre-split fields (first_name, last_name, etc.).
 * 
 * Security features:
 * - Hardcoded file path prevents path traversal attacks
 * - Maximum line limit (10,000) prevents denial of service attacks
 * - Explicit UTF-8 encoding ensures consistent character handling
 * - Specific IOException handling for proper error reporting
 */
@Component
@Slf4j
public class SourceAPatientParser {

    private static final String FILE_PATH = "data/source_a_patients.csv";
    private static final int MAX_LINES = 10_000;

    /**
     * Parses the Source A CSV file and extracts patient records.
     * 
     * CSV Format:
     * patient_id,first_name,last_name,dob,gender,phone,email,street,city,state,zip
     * PA001,John,Smith,1985-03-12,M,555-1010,john@test.com,12 Oak Ave,Boston,MA,02101
     * 
     * Process:
     * 1. Reads CSV file from classpath
     * 2. Skips header row
     * 3. Parses each data row by splitting on commas
     * 4. Maps tokens to field names using AppConstants
     * 5. Enforces maximum line limit to prevent DoS attacks
     * 
     * @return A list of maps, each representing a patient record with field names as keys
     * @throws RuntimeException if file cannot be read or exceeds maximum lines
     */
    public List<Map<String, String>> parse() {
        log.info("Reading Source A file: source_a_patients.csv");
        List<Map<String, String>> records = new ArrayList<>();
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new ClassPathResource(FILE_PATH).getInputStream(),
                                StandardCharsets.UTF_8
                        )
                )
        ) {
            reader.readLine(); // skip header
            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {
                // Prevent denial of service by limiting file size
                if (++lineCount > MAX_LINES)
                    throw new IOException("File exceeds maximum allowed lines: " + MAX_LINES);
                
                String[] tokens = line.split(",");
                Map<String, String> row = new HashMap<>();
                row.put(AppConstants.PATIENT_ID, tokens[0]);
                row.put(AppConstants.FIRST_NAME, tokens[1]);
                row.put(AppConstants.LAST_NAME, tokens[2]);
                row.put(AppConstants.DOB, tokens[3]);
                row.put(AppConstants.GENDER, tokens[4]);
                row.put(AppConstants.PHONE, tokens[5]);
                row.put(AppConstants.EMAIL, tokens[6]);
                row.put(AppConstants.STREET, tokens[7]);
                row.put(AppConstants.CITY, tokens[8]);
                row.put(AppConstants.STATE, tokens[9]);
                row.put(AppConstants.ZIP, tokens[10]);
                records.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse source_a_patients.csv", e);
        }
        log.info("Parsed {} records from Source A", records.size());
        return records;
    }
}