package se.inera.intyg.intygmockservice.application.navigation.staff;

public record StaffResponse(
    String staffId, String fullName, String prescriptionCode, String unitId) {}
