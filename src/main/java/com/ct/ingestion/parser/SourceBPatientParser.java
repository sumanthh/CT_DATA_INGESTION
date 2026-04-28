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
public class SourceBPatientParser {

    private static final String FILE_PATH = "data/source_b_patients.tsv";
    private static final int MAX_LINES = 10_000;

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