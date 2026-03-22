package se.inera.intyg.intygmockservice.application.navigation.certificate;

import java.time.LocalDateTime;

public record CertificateResponse(
    String certificateId,
    String certificateType,
    String certificateTypeDisplayName,
    LocalDateTime signingTimestamp,
    LocalDateTime sentTimestamp,
    String version,
    String logicalAddress,
    PatientData patient,
    StaffData issuedBy) {

  public record PatientData(
      String personId,
      String firstName,
      String lastName,
      String streetAddress,
      String postalCode,
      String city) {}

  public record StaffData(
      String staffId, String fullName, String prescriptionCode, UnitData unit) {}

  public record UnitData(
      String unitId,
      String unitName,
      String streetAddress,
      String postalCode,
      String city,
      String phone,
      String email,
      CareProviderData careProvider) {}

  public record CareProviderData(String careProviderId, String careProviderName) {}
}
