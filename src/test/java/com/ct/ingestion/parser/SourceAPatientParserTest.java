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
    void parseRuntimeException() {

        try (MockedConstruction<ClassPathResource> mocked =
                     mockConstruction(ClassPathResource.class,
                             (mock, context) ->
                                     when(mock.getInputStream())
                                             .thenThrow(new RuntimeException("IO error"))
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
