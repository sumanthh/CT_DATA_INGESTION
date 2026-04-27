package com.ct.fhir.dao;

import com.ct.fhir.model.PatientEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void saveAndFindById_success() {

        PatientEntity patient = buildPatient(
                "PA001",
                "SOURCE_A"
        );

        patientRepository.save(patient);

        Optional<PatientEntity> result =
                patientRepository.findById("PA001");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void findBySource_success() {
        patientRepository.save(buildPatient("PA001", "SOURCE_A"));
        patientRepository.save(buildPatient("PA002", "SOURCE_A"));
        patientRepository.save(buildPatient("PB001", "SOURCE_B"));

        List<PatientEntity> sourceAResults =
                patientRepository.findBySource("SOURCE_A");

        assertThat(sourceAResults).hasSize(2);
    }

    @Test
    void findBySource_noResults() {
        patientRepository.save(buildPatient("PA001", "SOURCE_A"));
        List<PatientEntity> result =
                patientRepository.findBySource("SOURCE_X");
        assertThat(result).isEmpty();
    }

    private PatientEntity buildPatient(String id, String source) {
        PatientEntity p = new PatientEntity();
        p.setId(id);
        p.setSource(source);
        p.setFirstName("John");
        p.setLastName("Smith");
        p.setBirthDate("1985-03-12");
        p.setGender("male");
        p.setPhone("555-101-2020");
        p.setEmail("john.smith@test.com");
        p.setAddressLine("12 Oak Ave");
        p.setCity("Boston");
        p.setState("MA");
        p.setZip("02101");
        return p;
    }
}