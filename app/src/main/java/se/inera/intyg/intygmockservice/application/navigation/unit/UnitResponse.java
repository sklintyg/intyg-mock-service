package se.inera.intyg.intygmockservice.application.navigation.unit;

public record UnitResponse(
    String unitId,
    String unitName,
    String streetAddress,
    String postalCode,
    String city,
    String phone,
    String email,
    String careProviderId,
    String careProviderName) {}
