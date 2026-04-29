package com.ct;

import com.ct.ingestion.facade.PatientIngestionFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main entry point for the Patient Data Ingestion & FHIR API Spring Boot application.
 * 
 * This application supports two execution modes:
 * 1. Ingestion Mode: Reads patient data from CSV/TSV files, normalizes it, and persists to H2 database
 * 2. API Mode: Starts a REST API server to retrieve patient data in FHIR R4 format
 * 
 */
@SpringBootApplication(scanBasePackages = "com.ct")
@EnableJpaRepositories(basePackages = "com.ct.fhir.dao")
@EntityScan(basePackages = "com.ct.fhir.model")
@Slf4j
public class Application {

    /**
     * Main method to start the Spring Boot application.
     * 
     * @param args Command-line arguments. Use --spring.profiles.active=ingestion for ingestion mode
     */
    public static void main(String[] args) {
        log.info("Starting Application");
        SpringApplication.run(Application.class, args);
    }

    /**
     * Creates a CommandLineRunner bean that executes patient ingestion on application startup.
     * This bean is only active when the 'ingestion' profile is enabled.
     * 
     * Usage: ./gradlew bootRun --args="--spring.profiles.active=ingestion"
     * 
     * @param ingestionFacade The facade that orchestrates the ingestion process
     * @return A CommandLineRunner that triggers ingestion of patient data from both sources
     */
    @Bean
    @Profile("ingestion")
    CommandLineRunner runIngestion(PatientIngestionFacade ingestionFacade) {
        return args -> ingestionFacade.ingestAll();
    }
}



