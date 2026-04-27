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
    void getAllPatientsSourceIsNull() {

        PatientEntity entity = buildPatient("PA001", "SOURCE_A");
        Map<String, Object> fhirPatient =
                Map.of("resourceType", "Patient", "id", "PA001");

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toFhir(entity)).thenReturn(fhirPatient);

        List<Map<String, Object>> result =
                service.getAllPatients(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).get("id")).isEqualTo("PA001");

        verify(repository).findAll();
        verify(mapper).toFhir(entity);
    }


    @Test
    void getAllPatientsSourceProvided() {

        PatientEntity entity = buildPatient("PA002", "SOURCE_A");
        Map<String, Object> fhirPatient =
                Map.of("resourceType", "Patient", "id", "PA002");

        when(repository.findBySource("SOURCE_A"))
                .thenReturn(List.of(entity));
        when(mapper.toFhir(entity)).thenReturn(fhirPatient);

        List<Map<String, Object>> result =
                service.getAllPatients("SOURCE_A");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).get("id")).isEqualTo("PA002");

        verify(repository).findBySource("SOURCE_A");
    }

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