# Patient Data Ingestion & FHIR API - Take-Home Assignment

## Overview
Healthcare systems receive patient data from multiple upstream sources — each with its own file format, naming conventions, and encoding rules.
This project demonstrates how to:
- Ingest heterogeneous patient data from two different files
- Normalize it into a unified internal schema
- Persist it in a file-based database (H2 file mode)
- Expose it via a Spring Boot REST API returning FHIR R4 Patient resources

The system is fully **idempotent** — running ingestion multiple times does not create duplicate records.

---------------------------------------------
## Project Structure
src/
 ├── main/
 │    ├── java/com/ct/... (ingestion, mappers, API, DAO)
 │    └── resources/data/ (source_a_patients.csv, source_b_patients.tsv)
 └── test/...

---------------------------------------------
## Unified Internal Schema (patients table)
Columns:
- id (TEXT, primary key)
- source (TEXT)
- first_name
- last_name
- birth_date (YYYY-MM-DD)
- gender (male/female/unknown)
- phone
- email (nullable)
- address_line
- city
- state
- zip (nullable)

---------------------------------------------
## Part 1: Running the Ingestion Script
1. Build the project:
   ./gradlew clean build

2. Run ingestion:
   ./gradlew bootRun --args="--spring.profiles.active=ingestion"

3. Logs should show:
   Patient ingestion STARTED
   Reading Source A file: source_a_patients.csv
   Parsed 10 records from Source A
   Source A records read = 10
   Reading Source B file: source_b_patients.tsv
   Parsed 10 records from Source B
   Source B records read = 10
   Patient ingestion COMPLETED. Total records in DB: 20

4. Database file location:
   ./db/patient-db.mv.db

5. Optional: Open H2 Console:
   URL: http://localhost:8080/h2-console
   JDBC: jdbc:h2:file:./db/patient-db
   User: sa

---------------------------------------------
## Part 2: Running the REST API
Start the app:
   ./gradlew bootRun

API Base URL:
   http://localhost:8080

---------------------------------------------
## API Endpoints
### GET /patients
Returns all 20 patients as FHIR R4 Patient JSON.
Example:
[
  {
    "resourceType": "Patient",
    "id": "PA001",
    "name": [{"use": "official", "family": "Smith", "given": ["John"]}],
    "gender": "male",
    "birthDate": "1985-03-12",
    "telecom": [{"system": "phone", "value": "555-101-2020"}, {"system": "email", "value": "john@test.com"}],
    "address": [{"line": ["12 Oak Ave"], "city": "Boston", "state": "MA", "postalCode": "02101"}]
  }
]

### GET /patients/{id}
Returns a single patient or HTTP 404 with OperationOutcome.

Curl:
   curl http://localhost:8080/patients/PA001

---------------------------------------------
## Normalization Rules
Source A → Already split fields
Source B → Requires:
- name split (first + last)
- date format conversion DD/MM/YYYY → YYYY-MM-DD
- gender mapping M/F → male/female
- email/zip set to null

---------------------------------------------
End of README
