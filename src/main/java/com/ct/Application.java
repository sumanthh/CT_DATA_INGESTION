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

@SpringBootApplication(scanBasePackages = "com.ct")
@EnableJpaRepositories(basePackages = "com.ct.fhir.dao")
@EntityScan(basePackages = "com.ct.fhir.model")
@Slf4j
public class Application {

    public static void main(String[] args) {
        log.info("Starting Application");
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Profile("ingestion")
    CommandLineRunner runIngestion(PatientIngestionFacade ingestionFacade) {
        return args -> ingestionFacade.ingestAll();
    }
}



