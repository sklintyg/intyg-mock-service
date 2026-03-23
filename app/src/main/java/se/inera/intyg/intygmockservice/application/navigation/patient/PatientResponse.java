package se.inera.intyg.intygmockservice.application.navigation.patient;

public record PatientResponse(
    String personId,
    String firstName,
    String lastName,
    String streetAddress,
    String postalCode,
    String city) {}
