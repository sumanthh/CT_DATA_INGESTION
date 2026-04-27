package com.ct.fhir.mapper;

import com.ct.fhir.model.PatientEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for FhirPatientMapper.
 * MockitoExtension is used for consistency across tests.
 */
@ExtendWith(MockitoExtension.class)
class FhirPatientMapperTest {

    private final FhirPatientMapper mapper = new FhirPatientMapper();

    @Test
    void mapAllFields() {
        PatientEntity entity = buildPatient(true, true);
        Map<String, Object> result = mapper.toFhir(entity);
        assertThat(result.get("resourceType")).isEqualTo("Patient");
        assertThat(result.get("id")).isEqualTo("PA001");
        assertThat(result.get("gender")).isEqualTo("male");
        assertThat(result.get("birthDate")).isEqualTo("1985-03-12");
        List<Map<String, Object>> names =
                (List<Map<String, Object>>) result.get("name");

        assertThat(names).hasSize(1);
        assertThat(names.get(0).get("family")).isEqualTo("Smith");

        List<Map<String, Object>> telecom =
                (List<Map<String, Object>>) result.get("telecom");

        assertThat(telecom).hasSize(2); // phone + email

        List<Map<String, Object>> addresses =
                (List<Map<String, Object>>) result.get("address");

        assertThat(addresses).hasSize(1);
        assertThat(addresses.get(0).get("postalCode")).isEqualTo("02101");
    }

    @Test
    void excludeEmailAndZipNull() {
        PatientEntity entity = buildPatient(false, false);
        Map<String, Object> result = mapper.toFhir(entity);
        List<Map<String, Object>> telecom =
                (List<Map<String, Object>>) result.get("telecom");
        assertThat(telecom).hasSize(1); // only phone
        List<Map<String, Object>> addresses =
                (List<Map<String, Object>>) result.get("address");
        assertThat(addresses.get(0))
                .doesNotContainKey("postalCode");
    }

    private PatientEntity buildPatient(boolean withEmail, boolean withZip) {
        PatientEntity p = new PatientEntity();
        p.setId("PA001");
        p.setFirstName("John");
        p.setLastName("Smith");
        p.setGender("male");
        p.setBirthDate("1985-03-12");
        p.setPhone("555-1010");
        p.setAddressLine("12 Oak Ave");
        p.setCity("Boston");
        p.setState("MA");

        if (withEmail) {
            p.setEmail("john.smith@test.com");
        }

        if (withZip) {
            p.setZip("02101");
        }
        return p;
    }
}