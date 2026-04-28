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
    void fromSourceA_allFields() {
        Map<String, String> raw = Map.of(
                "patient_id", "PA003",
                "SOURCE", "SOURCE_A",
                "first_name", "Alice",
                "last_name", "Brown",
                "dob", "1990-06-15",
                "gender", "female",
                "phone", "555-9999",
                "email", "alice@test.com",
                "street", "10 Elm St",
                "city", "Chicago"
        );

        PatientEntity entity = normalizer.fromSourceA(raw);

        assertThat(entity.getBirthDate()).isEqualTo("1990-06-15");
        assertThat(entity.getGender()).isEqualTo("female");
        assertThat(entity.getPhone()).isEqualTo("555-9999");
        assertThat(entity.getEmail()).isEqualTo("alice@test.com");
        assertThat(entity.getAddressLine()).isEqualTo("10 Elm St");
        assertThat(entity.getCity()).isEqualTo("Chicago");
    }

    @Test
    void normalizeGender_nullValue_returnsUnknown() {
        Map<String, String> raw = Map.of(
                "patient_id", "PA004",
                "first_name", "Sam",
                "last_name", "Lee"
        );

        PatientEntity entity = normalizer.fromSourceA(raw);

        assertThat(entity.getGender()).isEqualTo("unknown");
    }

    @Test
    void normalizeGender_longFormMale_returnsMale() {
        Map<String, String> raw = Map.of(
                "patient_id", "PA005",
                "first_name", "Tom",
                "last_name", "Jones",
                "gender", "MALE"
        );

        assertThat(normalizer.fromSourceA(raw).getGender()).isEqualTo("male");
    }

    @Test
    void normalizeGender_longFormFemale_returnsFemale() {
        Map<String, String> raw = Map.of(
                "patient_id", "PA006",
                "first_name", "Nina",
                "last_name", "Ross",
                "gender", "FEMALE"
        );

        assertThat(normalizer.fromSourceA(raw).getGender()).isEqualTo("female");
    }

    @Test
    void fromSourceB_genderF_returnsFemale() {
        Map<String, String> raw = Map.of(
                "id", "PB003",
                "full_name", "Laura Hill",
                "birth_date", "10/05/1985",
                "sex", "F",
                "contact_number", "555-4040",
                "addr_line", "5 Maple Ave",
                "addr_city", "Austin",
                "addr_state", "TX"
        );

        assertThat(normalizer.fromSourceB(raw).getGender()).isEqualTo("female");
    }

    @Test
    void fromSourceB_genderM_returnsMale() {
        Map<String, String> raw = Map.of(
                "id", "PB004",
                "full_name", "Mark Stone",
                "birth_date", "15/08/1978",
                "sex", "M",
                "contact_number", "555-5050",
                "addr_line", "9 River Rd",
                "addr_city", "Denver",
                "addr_state", "CO"
        );

        PatientEntity entity = normalizer.fromSourceB(raw);

        assertThat(entity.getGender()).isEqualTo("male");
        assertThat(entity.getBirthDate()).isEqualTo("1978-08-15");
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