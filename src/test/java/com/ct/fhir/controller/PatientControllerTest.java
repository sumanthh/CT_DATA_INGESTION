
package com.ct.fhir.controller;

import com.ct.Application;
import com.ct.fhir.exception.PatientNotFoundException;
import com.ct.fhir.service.PatientService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PatientService patientService;

    /*@Test
    void getAllPatients_success() throws Exception {
        Mockito.when(patientService.getAllPatients(null))
                .thenReturn(List.of(
                        Map.of("resourceType", "Patient", "id", "PA001"),
                        Map.of("resourceType", "Patient", "id", "PA002")
                ));

        mockMvc.perform(get("/api/patients")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }*/

    @Test
    void getAllBundlePatients_success() throws Exception {
        Mockito.when(patientService.getAllBundlePatients(null))
                .thenReturn(List.of(
                        Map.of("resourceType", "Patient", "id", "PA001"),
                        Map.of("resourceType", "Patient", "id", "PA002")
                ));

        mockMvc.perform(get("/api/patients")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getBundlePatients_withUnknownSource_returnsEmptyBundle() throws Exception {
        mockMvc.perform(get("/api/patients")
                        .param("source", "UNKNOWN_SOURCE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resourceType").value("Bundle"))
                .andExpect(jsonPath("$[0].total").value(0))
                .andExpect(jsonPath("$[0].entry", hasSize(0)));
    }

    @Test
    void getPatientById_notFound() throws Exception {

        Mockito.when(patientService.getPatientById("INVALID"))
                .thenThrow(new PatientNotFoundException(
                        "Patient with id INVALID not found"
                ));

        mockMvc.perform(get("/api/patients/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resourceType")
                        .value("OperationOutcome"));
    }
}
