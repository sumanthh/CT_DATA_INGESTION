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

@Component
@Slf4j
public class SourceAPatientParser {

    private static final String FILE_PATH = "data/source_a_patients.csv";
    private static final int MAX_LINES = 10_000;

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