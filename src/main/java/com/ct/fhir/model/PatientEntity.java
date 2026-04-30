package com.ct.fhir.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA Entity representing a patient record in the database.
 * 
 * Maps to the 'patients' table in H2 database.
 * Contains unified patient data normalized from multiple sources (Source A and Source B).
 * 
 * Schema:
 * - id: Primary key, unique patient identifier (e.g., PA001, PB001)
 * - source: Data source identifier (SOURCE_A or SOURCE_B)
 * - firstName, lastName: Patient name components
 * - birthDate: Date of birth in YYYY-MM-DD format
 * - gender: Gender value (male, female, unknown)
 * - phone: Contact phone number
 * - email: Contact email (nullable)
 * - addressLine, city, state, zip: Address components (zip is nullable)
 */
@Entity
@Table(name = "patients")
public class PatientEntity {

    @Id
    @Column(nullable = false, length = 50)
    private String id;

    @Column(nullable = false)
    private String source;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private String birthDate;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String phone;

    private String email;

    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    private String zip;

    /**
     * Gets the unique patient identifier.
     * @return The patient ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique patient identifier.
     * @param id The patient ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the data source identifier.
     * @return The source (SOURCE_A or SOURCE_B)
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the data source identifier.
     * @param source The source identifier
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets the patient's first name.
     * @return The first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the patient's first name.
     * @param firstName The first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the patient's last name.
     * @return The last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the patient's last name.
     * @param lastName The last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the patient's birth date.
     * @return The birth date in YYYY-MM-DD format
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the patient's birth date.
     * @param birthDate The birth date in YYYY-MM-DD format
     */
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Gets the patient's gender.
     * @return The gender (male, female, unknown)
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the patient's gender.
     * @param gender The gender value
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets the patient's phone number.
     * @return The phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the patient's phone number.
     * @param phone The phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the patient's email address.
     * @return The email address (may be null)
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the patient's email address.
     * @param email The email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the patient's address line.
     * @return The street address
     */
    public String getAddressLine() {
        return addressLine;
    }

    /**
     * Sets the patient's address line.
     * @param addressLine The street address
     */
    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    /**
     * Gets the patient's city.
     * @return The city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the patient's city.
     * @param city The city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the patient's state.
     * @return The state code
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the patient's state.
     * @param state The state code
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the patient's postal code.
     * @return The postal code (may be null)
     */
    public String getZip() {
        return zip;
    }

    /**
     * Sets the patient's postal code.
     * @param zip The postal code
     */
    public void setZip(String zip) {
        this.zip = zip;
    }
}