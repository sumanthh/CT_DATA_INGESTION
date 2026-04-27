package com.ct.ingestion.facade;

import com.ct.fhir.dao.PatientRepository;
import com.ct.fhir.model.PatientEntity;
import com.ct.ingestion.mapper.PatientNormalizer;
import com.ct.ingestion.parser.SourceAPatientParser;
import com.ct.ingestion.parser.SourceBPatientParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Unit tests for PatientIngestionFacade using Mockito only.
 */
@ExtendWith(MockitoExtension.class)
class PatientIngestionFacadeTest {

    @Mock
    private PatientRepository repository;

    @Mock
    private SourceAPatientParser sourceAParser;

    @Mock
    private SourceBPatientParser sourceBParser;

    @Mock
    private PatientNormalizer normalizer;

    @InjectMocks
    private PatientIngestionFacade facade;

    @Test
    void ingestAllBothSources() {
        Map<String, String> sourceARecord = Map.of("id", "PA001");
        Map<String, String> sourceBRecord = Map.of("id", "PB001");

        when(sourceAParser.parse())
                .thenReturn(List.of(sourceARecord));
        when(sourceBParser.parse())
                .thenReturn(List.of(sourceBRecord));

        PatientEntity paEntity = new PatientEntity();
        paEntity.setId("PA001");

        PatientEntity pbEntity = new PatientEntity();
        pbEntity.setId("PB001");

        when(normalizer.fromSourceA(sourceARecord))
                .thenReturn(paEntity);
        when(normalizer.fromSourceB(sourceBRecord))
                .thenReturn(pbEntity);

        when(repository.existsById("PA001")).thenReturn(false);
        when(repository.existsById("PB001")).thenReturn(false);
        when(repository.count()).thenReturn(2L);

        facade.ingestAll();

        verify(sourceAParser).parse();
        verify(sourceBParser).parse();

        verify(repository).save(paEntity);
        verify(repository).save(pbEntity);

        verify(repository).count();
    }

    @Test
    void ingestAllExistingPatient() {

        Map<String, String> sourceARecord = Map.of("id", "PA002");

        PatientEntity existingPatient = new PatientEntity();
        existingPatient.setId("PA002");

        when(sourceAParser.parse())
                .thenReturn(List.of(sourceARecord));
        when(sourceBParser.parse())
                .thenReturn(List.of());
        when(normalizer.fromSourceA(sourceARecord))
                .thenReturn(existingPatient);

        when(repository.existsById("PA002"))
                .thenReturn(true);

        facade.ingestAll();

        verify(repository, never()).save(any());
        verify(repository).existsById("PA002");
    }

    @Test
    void ingestAllEmpty() {

        when(sourceAParser.parse()).thenReturn(List.of());
        when(sourceBParser.parse()).thenReturn(List.of());
        when(repository.count()).thenReturn(0L);

        facade.ingestAll();

        verify(repository, never()).save(any());
        verify(repository).count();
    }
}
