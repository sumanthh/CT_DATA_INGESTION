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
 * Parser component for Source B patient data (TSV format).
 * 
 * Reads and parses the source_b_patients.tsv file from the classpath.
 * Extracts patient records with full names that need to be split into first and last names.
 * 
 * Security features:
 * - Hardcoded file path prevents path traversal attacks
 * - Maximum line limit (10,000) prevents denial of service attacks
 * - Explicit UTF-8 encoding ensures consistent character handling
 * - Specific IOException handling for proper error reporting
 */
@Component
@Slf4j
public class SourceBPatientParser {

    private static final String FILE_PATH = "data/source_b_patients.tsv";
    private static final int MAX_LINES = 10_000;

    /**
     * Parses the Source B TSV file and extracts patient records.
     * 
     * TSV Format (tab-separated):
     * id\tfull_name\tbirth_date\tsex\tcontact_number\taddr_line\taddr_city\taddr_state
     * PB001\tJane Doe\t25/12/1990\tF\t555-2020\t34 Pine St\tSeattle\tWA
     * 
     * Process:
     * 1. Reads TSV file from classpath
     * 2. Skips header row
     * 3. Parses each data row by splitting on tabs
     * 4. Maps tokens to field names using AppConstants
     * 5. Enforces maximum line limit to prevent DoS attacks
     * 
     * Note: Source B data requires normalization:
     * - Full names must be split into first and last names
     * - Dates must be converted from DD/MM/YYYY to YYYY-MM-DD
     * - Gender values (M/F) must be mapped to (male/female)
     * 
     * @return A list of maps, each representing a patient record with field names as keys
     * @throws RuntimeException if file cannot be read or exceeds maximum lines
     */
    public List<Map<String, String>> parse() {
        log.info("Reading Source B file: source_b_patients.tsv");
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
                
                String[] tokens = line.split("\t");

                Map<String, String> row = new HashMap<>();
                row.put(AppConstants.ID, tokens[0]);
                row.put(AppConstants.FULL_NAME, tokens[1]);
                row.put(AppConstants.BIRTH_DATE, tokens[2]);
                row.put(AppConstants.SEX, tokens[3]);
                row.put(AppConstants.CONTACT_NUMBER, tokens[4]);
                row.put(AppConstants.ADDR_LINE, tokens[5]);
                row.put(AppConstants.ADDR_CITY, tokens[6]);
                row.put(AppConstants.ADDR_STATE, tokens[7]);

                records.add(row);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse source_b_patients.tsv", e);
        }
        log.info("Parsed {} records from Source B", records.size());
        return records;
    }
}