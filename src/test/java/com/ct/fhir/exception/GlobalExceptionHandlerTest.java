package com.ct.fhir.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handlePatientNotFound() {
        PatientNotFoundException exception =
                new PatientNotFoundException("PA001");

        ResponseEntity<Map<String, Object>> response =
                handler.handlePatientNotFound(exception);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("resourceType"))
                .isEqualTo("OperationOutcome");

        List<Map<String, Object>> issues =
                (List<Map<String, Object>>) body.get("issue");

        assertThat(issues).hasSize(1);

        Map<String, Object> issue = issues.get(0);
        assertThat(issue.get("severity")).isEqualTo("error");
        assertThat(issue.get("code")).isEqualTo("not-found");
        assertThat(issue.get("diagnostics"))
                .isEqualTo("Patient with id PA001 not found");
    }
}
