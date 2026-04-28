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
    void parse_headerOnly_returnsEmptyList() throws Exception {
        String tsvData = "id\tfull_name\tbirth_date\tsex\tcontact_number\taddr_line\taddr_city\taddr_state\n";

        try (MockedConstruction<ClassPathResource> mocked =
                     mockConstruction(ClassPathResource.class,
                             (mock, context) -> when(mock.getInputStream())
                                     .thenReturn(new ByteArrayInputStream(
                                             tsvData.getBytes(StandardCharsets.UTF_8)))
                     )) {

            List<Map<String, String>> records = parser.parse();

            assertThat(records).isEmpty();
        }
    }

    @Test
    void parse_multipleRows_returnsAllRecords() throws Exception {
        String tsvData =
                "id\tfull_name\tbirth_date\tsex\tcontact_number\taddr_line\taddr_city\taddr_state\n" +
                "PB001\tJane Doe\t25/12/1990\tF\t555-2020\t34 Pine St\tSeattle\tWA\n" +
                "PB002\tMark Stone\t15/08/1978\tM\t555-3030\t9 River Rd\tDenver\tCO\n";

        try (MockedConstruction<ClassPathResource> mocked =
                     mockConstruction(ClassPathResource.class,
                             (mock, context) -> when(mock.getInputStream())
                                     .thenReturn(new ByteArrayInputStream(
                                             tsvData.getBytes(StandardCharsets.UTF_8)))
                     )) {

            List<Map<String, String>> records = parser.parse();

            assertThat(records).hasSize(2);
            assertThat(records.get(1).get("id")).isEqualTo("PB002");
            assertThat(records.get(1).get("full_name")).isEqualTo("Mark Stone");
            assertThat(records.get(1).get("addr_city")).isEqualTo("Denver");
        }
    }

    @Test
    void parse_addrLineFieldMapped() throws Exception {
        String tsvData =
                "id\tfull_name\tbirth_date\tsex\tcontact_number\taddr_line\taddr_city\taddr_state\n" +
                "PB003\tAlice Brown\t10/05/1985\tF\t555-4040\t5 Maple Ave\tAustin\tTX\n";

        try (MockedConstruction<ClassPathResource> mocked =
                     mockConstruction(ClassPathResource.class,
                             (mock, context) -> when(mock.getInputStream())
                                     .thenReturn(new ByteArrayInputStream(
                                             tsvData.getBytes(StandardCharsets.UTF_8)))
                     )) {

            Map<String, String> row = parser.parse().get(0);

            assertThat(row.get("addr_line")).isEqualTo("5 Maple Ave");
            assertThat(row.get("addr_state")).isEqualTo("TX");
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

            RuntimeException exception = catchThrowableOfType(
                    parser::parse,
                    RuntimeException.class
            );

            assertThat(exception)
                    .hasMessage("Failed to parse source_b_patients.tsv")
                    .hasCauseInstanceOf(java.io.IOException.class);
        }
    }
}