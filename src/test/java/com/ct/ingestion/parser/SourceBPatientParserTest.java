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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SourceBPatientParserTest {

    private final SourceBPatientParser parser = new SourceBPatientParser();

    @Test
    void parseTsv() throws Exception {

        String tsvData =
                "id\tfull_name\tbirth_date\tsex\tcontact_number\taddr_line\taddr_city\taddr_state\n" +
                        "PB001\tJane Doe\t25/12/1990\tF\t555-2020\t34 Pine St\tSeattle\tWA\n";

        try (MockedConstruction<ClassPathResource> mocked =
                     mockConstruction(ClassPathResource.class,
                             (mock, context) -> when(mock.getInputStream())
                                     .thenReturn(new ByteArrayInputStream(
                                             tsvData.getBytes(StandardCharsets.UTF_8)))
                     )) {

            List<Map<String, String>> records = parser.parse();

            assertThat(records).hasSize(1);

            Map<String, String> row = records.get(0);
            assertThat(row.get("id")).isEqualTo("PB001");
            assertThat(row.get("full_name")).isEqualTo("Jane Doe");
            assertThat(row.get("birth_date")).isEqualTo("25/12/1990");
            assertThat(row.get("sex")).isEqualTo("F");
            assertThat(row.get("contact_number")).isEqualTo("555-2020");
            assertThat(row.get("addr_city")).isEqualTo("Seattle");
            assertThat(row.get("addr_state")).isEqualTo("WA");
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

            RuntimeException exception = catchThrowableOfType(
                    parser::parse,
                    RuntimeException.class
            );

            assertThat(exception)
                    .hasMessage("Failed to parse source_b_patients.tsv")
                    .hasCauseInstanceOf(RuntimeException.class);
        }
    }
}