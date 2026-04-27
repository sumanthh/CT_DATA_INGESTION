package com.ct.ingestion.parser;

import com.ct.fhir.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Component
@Slf4j
public class SourceBPatientParser {
    public List<Map<String, String>> parse() {
        log.info("Reading Source B file: source_b_patients.tsv");
        List<Map<String, String>> records = new ArrayList<>();
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new ClassPathResource("data/source_b_patients.tsv")
                                        .getInputStream()
                        )
                )
        ) {
            String header = reader.readLine(); // skip header
            String line;

            while ((line = reader.readLine()) != null) {
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

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse source_b_patients.tsv", e);
        }
        log.info("Parsed {} records from Source B", records.size());
        return records;
    }
}