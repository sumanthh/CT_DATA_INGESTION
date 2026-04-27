package com.ct.fhir.dao;


import com.ct.fhir.model.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, String> {

    List<PatientEntity> findBySource(String source);

}
