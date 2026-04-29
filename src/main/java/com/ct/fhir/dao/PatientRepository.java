package com.ct.fhir.dao;

import com.ct.fhir.model.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Data Access Object (DAO) for patient database operations.
 * 
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 * Spring Data JPA automatically generates implementations for all methods.
 * 
 */
@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, String> {

    /**
     * Retrieves all patients from a specific data source.
     * 
     * @param source The data source identifier (SOURCE_A or SOURCE_B)
     * @return A list of patients from the specified source
     */
    List<PatientEntity> findBySource(String source);

}
