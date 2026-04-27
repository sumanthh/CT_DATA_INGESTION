package com.ct.fhir.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class PatientNotFoundExceptionTest {

    @Test
    void createException() {
        String patientId = "PA001";

        PatientNotFoundException exception =
                new PatientNotFoundException(patientId);
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage())
                .isEqualTo("Patient with id PA001 not found");
    }
}
