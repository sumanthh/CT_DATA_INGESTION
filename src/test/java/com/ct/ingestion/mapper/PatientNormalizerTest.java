package com.ct.ingestion.mapper;

import com.ct.fhir.model.PatientEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PatientNormalizer.
 * MockitoExtension is used for consistency, no mocks required.
 */
@ExtendWith(MockitoExtension.class)
class PatientNormalizerTest {

    private final PatientNormalizer normalizer = new PatientNormalizer();

    @Test
    void fromSourceA() {
        Map<String, String> raw = Map.of("patient_id", "PA001",
                "SOURCE","SOURCE_A",
                "first_name", "John",
                "last_name", "Smith"
        );

        PatientEntity entity = normalizer.fromSourceA(raw);

        assertThat(entity.getId()).isEqualTo("PA001");
        assertThat(entity.getSource()).isEqualTo("SOURCE_A");
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Smith");

    }

    @Test
    void fromSourceB() {
        Map<String, String> raw = Map.of(
                "id", "PB001",
                "full_name", "Jane Doe",
                "birth_date", "25/12/1990",
                "sex", "female",
                "contact_number", "555-2020",
                "addr_line", "34 Pine Street",
                "addr_city", "Seattle",
                "addr_state", "WA"
        );
        PatientEntity entity = normalizer.fromSourceB(raw);
        assertThat(entity.getId()).isEqualTo("PB001");
        assertThat(entity.getSource()).isEqualTo("SOURCE_B");
        assertThat(entity.getFirstName()).isEqualTo("Jane");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getBirthDate()).isEqualTo("1990-12-25");
        assertThat(entity.getGender()).isEqualTo("female");
        assertThat(entity.getEmail()).isNull();
        assertThat(entity.getZip()).isNull();
    }

    @Test
    void normalizeGender() {

        Map<String, String> raw = Map.of(
                "patient_id", "PA002",
                "first_name", "Alex",
                "last_name", "Taylor",
                "dob", "1995-01-01",
                "gender", "X"
        );

        PatientEntity entity = normalizer.fromSourceA(raw);

        assertThat(entity.getGender()).isEqualTo("unknown");
    }

    @Test
    void fromSourceBSingleName() {

        Map<String, String> raw = Map.of(
                "id", "PB002",
                "full_name", "Prince",
                "birth_date", "01/01/1980",
                "sex", "M",
                "contact_number", "555-3030",
                "addr_line", "Unknown",
                "addr_city", "Unknown",
                "addr_state", "NA"
        );
        PatientEntity entity = normalizer.fromSourceB(raw);

        assertThat(entity.getFirstName()).isEqualTo("Prince");
        assertThat(entity.getLastName()).isEqualTo("");
    }
}