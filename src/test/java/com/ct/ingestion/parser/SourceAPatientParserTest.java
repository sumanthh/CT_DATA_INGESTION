package com.ct.ingestion.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SourceAPatientParserTest {

    private final SourceAPatientParser parser = new SourceAPatientParser();

    @Test
    void parseCsv() throws Exception {
        String csvData =
                "patient_id,first_name,last_name,dob,gender,phone,email,street,city,state,zip\n" +
                        "PA001,John,Smith,1985-03-12,M,555-1010,john@test.com,12 Oak Ave,Boston,MA,02101";

        try (MockedConstruction<ClassPathResource> mocked =
                     mockConstruction(ClassPathResource.class,
                             (mock, context) -> when(mock.getInputStream())
                                     .thenReturn(new ByteArrayInputStream(
                                             csvData.getBytes(StandardCharsets.UTF_8)))
                     )) {

            List<Map<String, String>> records = parser.parse();

            assertThat(records).hasSize(1);

            Map<String, String> row = records.get(0);
            assertThat(row.get("patient_id")).isEqualTo("PA001");
            assertThat(row.get("first_name")).isEqualTo("John");
            assertThat(row.get("last_name")).isEqualTo("Smith");
            assertThat(row.get("city")).isEqualTo("Boston");
            assertThat(row.get("zip")).isEqualTo("02101");
        }
    }

    @Test
    void parse_headerOnly_returnsEmptyList() throws Exception {
        String csvData = "patient_id,first_name,last_name,dob,gender,phone,email,street,city,state,zip\n";

        try (MockedConstruction<ClassPathResource> mocked =
                     mockConstruction(ClassPathResource.class,
                             (mock, context) -> when(mock.getInputStream())
                                     .thenReturn(new ByteArrayInputStream(
                                             csvData.getBytes(StandardCharsets.UTF_8)))
                     )) {

            List<Map<String, String>> records = parser.parse();

            assertThat(records).isEmpty();
        }
    }

    @Test
    void parse_multipleRows_returnsAllRecords() throws Exception {
        String csvData =
                "patient_id,first_name,last_name,dob,gender,phone,email,street,city,state,zip\n" +
                "PA001,John,Smith,1985-03-12,M,555-1010,john@test.com,12 Oak Ave,Boston,MA,02101\n" +
                "PA002,Jane,Doe,1990-07-22,F,555-2020,jane@test.com,34 Pine St,Seattle,WA,98101";

        try (MockedConstruction<ClassPathResource> mocked =
                     mockConstruction(ClassPathResource.class,
                             (mock, context) -> when(mock.getInputStream())
                                     .thenReturn(new ByteArrayInputStream(
                                             csvData.getBytes(StandardCharsets.UTF_8)))
                     )) {

            List<Map<String, String>> records = parser.parse();

            assertThat(records).hasSize(2);
            assertThat(records.get(1).get("patient_id")).isEqualTo("PA002");
            assertThat(records.get(1).get("first_name")).isEqualTo("Jane");
            assertThat(records.get(1).get("city")).isEqualTo("Seattle");
        }
    }

    @Test
    void parse_allFieldsMapped() throws Exception {
        String csvData =
                "patient_id,first_name,last_name,dob,gender,phone,email,street,city,state,zip\n" +
                "PA003,Alice,Brown,1992-11-05,F,555-3030,alice@test.com,99 Elm St,Chicago,IL,60601";

        try (MockedConstruction<ClassPathResource> mocked =
                     mockConstruction(ClassPathResource.class,
                             (mock, context) -> when(mock.getInputStream())
                                     .thenReturn(new ByteArrayInputStream(
                                             csvData.getBytes(StandardCharsets.UTF_8)))
                     )) {

            Map<String, String> row = parser.parse().get(0);

            assertThat(row.get("dob")).isEqualTo("1992-11-05");
            assertThat(row.get("gender")).isEqualTo("F");
            assertThat(row.get("phone")).isEqualTo("555-3030");
            assertThat(row.get("email")).isEqualTo("alice@test.com");
            assertThat(row.get("street")).isEqualTo("99 Elm St");
            assertThat(row.get("state")).isEqualTo("IL");
            assertThat(row.get("zip")).isEqualTo("60601");
        }
    }

    @Test
    void parseRuntimeException() throws Exception {

        try (MockedConstruction<ClassPathResource> mocked =
                     mockConstruction(ClassPathResource.class,
                             (mock, context) ->
                                     when(mock.getInputStream())
                                             .thenThrow(new java.io.IOException("IO error"))
                     )) {

            RuntimeException ex = org.assertj.core.api.Assertions.catchThrowableOfType(
                    parser::parse,
                    RuntimeException.class
            );

            assertThat(ex.getMessage())
                    .isEqualTo("Failed to parse source_a_patients.csv");
        }
    }
}
