package com.ct.fhir.service;

import com.ct.fhir.dao.PatientRepository;
import com.ct.fhir.exception.PatientNotFoundException;
import com.ct.fhir.mapper.FhirPatientMapper;
import com.ct.fhir.model.PatientEntity;
import com.ct.fhir.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PatientServiceImpl using Mockito.
 */
@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository repository;

    @Mock
    private FhirPatientMapper mapper;

    @InjectMocks
    private PatientServiceImpl service;

    @Test
    void getPatientById_success() {

        PatientEntity entity = new PatientEntity();
        entity.setId("PA003");

        Map<String, Object> fhir = Map.of(
                "resourceType", "Patient",
                "id", "PA003"
        );

        when(repository.findById("PA003"))
                .thenReturn(Optional.of(entity));
        when(mapper.toFhir(entity)).thenReturn(fhir);

        Map<String, Object> result =
                service.getPatientById("PA003");

        assertThat(result.get("id")).isEqualTo("PA003");

        verify(repository).findById("PA003");
        verify(mapper).toFhir(entity);
    }

    @Test
    void getPatientByIdPatientInvalid() {
        when(repository.findById("INVALID"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getPatientById("INVALID"))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessage("Patient with id INVALID not found");

        verify(repository).findById("INVALID");
        verifyNoInteractions(mapper);
    }

    @Test
    void getAllBundlePatients_shouldReturnBundleInsideList() {
        PatientEntity p1 = new PatientEntity();
        p1.setId("PA001");

        PatientEntity p2 = new PatientEntity();
        p2.setId("PA002");

        Mockito.when(repository.findAll())
                .thenReturn(List.of(p1, p2));

        Map<String, Object> fhir1 = Map.of(
                "resourceType", "Patient",
                "id", "PA001"
        );

        Map<String, Object> fhir2 = Map.of(
                "resourceType", "Patient",
                "id", "PA002"
        );

        Mockito.when(mapper.toFhir(p1)).thenReturn(fhir1);
        Mockito.when(mapper.toFhir(p2)).thenReturn(fhir2);
        List<Map<String, Object>> result = service.getAllBundlePatients(null);
        assertThat(result).hasSize(1);
        Map<String, Object> bundle = result.get(0);
        assertThat(bundle.get("resourceType")).isEqualTo("Bundle");
        assertThat(bundle.get("type")).isEqualTo("collection");
        assertThat(bundle.get("total")).isEqualTo(2);

        List<Map<String, Object>> entries =
                (List<Map<String, Object>>) bundle.get("entry");

        assertThat(entries).hasSize(2);

        Map<String, Object> entry1 = entries.get(0);
        Map<String, Object> resource1 =
                (Map<String, Object>) entry1.get("resource");

        assertThat(resource1.get("id")).isEqualTo("PA001");

        Map<String, Object> entry2 = entries.get(1);
        Map<String, Object> resource2 =
                (Map<String, Object>) entry2.get("resource");

        assertThat(resource2.get("id")).isEqualTo("PA002");
    }

    private PatientEntity buildPatient(String id, String source) {
        PatientEntity p = new PatientEntity();
        p.setId(id);
        p.setSource(source);
        p.setFirstName("John");
        p.setLastName("Smith");
        p.setGender("male");
        p.setBirthDate("1985-03-12");
        p.setPhone("555-1010");
        return p;
    }
}