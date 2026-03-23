package se.inera.intyg.intygmockservice.application.navigation.certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.navigation.model.CareProvider;
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;
import se.inera.intyg.intygmockservice.domain.navigation.model.Staff;
import se.inera.intyg.intygmockservice.domain.navigation.model.Unit;

class CertificateAssemblerTest {

  private final CertificateAssembler assembler = new CertificateAssembler();

  @Test
  void toModel_ShouldIncludeSelfLink() {
    final var model = assembler.toModel(minimalCertificate("cert-001"));

    assertTrue(model.getLink("self").isPresent());
    assertTrue(
        model.getLink("self").get().getHref().contains("/api/navigate/certificates/cert-001"));
  }

  @Test
  void toModel_ShouldIncludeMessagesLink() {
    final var model = assembler.toModel(minimalCertificate("cert-001"));

    assertTrue(model.getLink("messages").isPresent());
    assertTrue(
        model
            .getLink("messages")
            .get()
            .getHref()
            .contains("/api/navigate/certificates/cert-001/messages"));
  }

  @Test
  void toModel_ShouldIncludeStatusUpdatesLink() {
    final var model = assembler.toModel(minimalCertificate("cert-001"));

    assertTrue(model.getLink("status-updates").isPresent());
  }

  @Test
  void toModel_ShouldIncludeLogEntriesLink() {
    final var model = assembler.toModel(minimalCertificate("cert-001"));

    assertTrue(model.getLink("log-entries").isPresent());
  }

  @Test
  void toModel_ShouldIncludeRevocationLink() {
    final var model = assembler.toModel(minimalCertificate("cert-001"));

    assertTrue(model.getLink("revocation").isPresent());
  }

  @Test
  void toModel_ShouldIncludePatientLinkWhenPersonIdPresent() {
    final var certificate =
        Certificate.builder()
            .certificateId("cert-001")
            .patient(Patient.builder().personId("191212121212").build())
            .build();

    final var model = assembler.toModel(certificate);

    assertTrue(model.getLink("patient").isPresent());
    assertTrue(
        model.getLink("patient").get().getHref().contains("/api/navigate/patients/191212121212"));
  }

  @Test
  void toModel_ShouldNotIncludePatientLinkWhenPatientIsNull() {
    final var model = assembler.toModel(minimalCertificate("cert-001"));

    assertTrue(model.getLink("patient").isEmpty());
  }

  @Test
  void toModel_ShouldIncludeUnitLinkWhenUnitIdPresent() {
    final var certificate =
        Certificate.builder()
            .certificateId("cert-001")
            .issuedBy(
                Staff.builder()
                    .staffId("staff-001")
                    .unit(Unit.builder().unitId("unit-001").build())
                    .build())
            .build();

    final var model = assembler.toModel(certificate);

    assertTrue(model.getLink("unit").isPresent());
    assertTrue(model.getLink("unit").get().getHref().contains("/api/navigate/units/unit-001"));
  }

  @Test
  void toModel_ShouldIncludeIssuerLinkWhenStaffIdPresent() {
    final var certificate =
        Certificate.builder()
            .certificateId("cert-001")
            .issuedBy(Staff.builder().staffId("staff-001").build())
            .build();

    final var model = assembler.toModel(certificate);

    assertTrue(model.getLink("issuer").isPresent());
    assertTrue(model.getLink("issuer").get().getHref().contains("/api/navigate/staff/staff-001"));
  }

  @Test
  void toModel_ShouldMapAllCertificateFieldsToResponse() {
    final var signingTime = LocalDateTime.of(2024, 3, 1, 12, 0);
    final var certificate =
        Certificate.builder()
            .certificateId("cert-001")
            .certificateType("LISJP")
            .certificateTypeDisplayName("Läkarintyg för sjukpenning")
            .signingTimestamp(signingTime)
            .version("1.0")
            .logicalAddress("FK")
            .build();

    final var response = assembler.toModel(certificate).getContent();

    assertNotNull(response);
    assertEquals("cert-001", response.certificateId());
    assertEquals("LISJP", response.certificateType());
    assertEquals("Läkarintyg för sjukpenning", response.certificateTypeDisplayName());
    assertEquals(signingTime, response.signingTimestamp());
    assertEquals("1.0", response.version());
    assertEquals("FK", response.logicalAddress());
  }

  @Test
  void toModel_ShouldMapPatientFieldsWhenPresent() {
    final var certificate =
        Certificate.builder()
            .certificateId("cert-001")
            .patient(
                Patient.builder()
                    .personId("191212121212")
                    .firstName("Test")
                    .lastName("Testsson")
                    .streetAddress("Testgatan 1")
                    .postalCode("12345")
                    .city("Teststaden")
                    .build())
            .build();

    final var response = assembler.toModel(certificate).getContent();

    assertNotNull(response);
    assertNotNull(response.patient());
    assertEquals("191212121212", response.patient().personId());
    assertEquals("Test", response.patient().firstName());
    assertEquals("Testsson", response.patient().lastName());
  }

  @Test
  void toModel_ShouldMapStaffAndUnitWhenPresent() {
    final var certificate =
        Certificate.builder()
            .certificateId("cert-001")
            .issuedBy(
                Staff.builder()
                    .staffId("staff-001")
                    .fullName("Dr Test")
                    .prescriptionCode("123")
                    .unit(
                        Unit.builder()
                            .unitId("unit-001")
                            .unitName("Test Unit")
                            .careProvider(
                                CareProvider.builder()
                                    .careProviderId("vg-001")
                                    .careProviderName("Test VG")
                                    .build())
                            .build())
                    .build())
            .build();

    final var response = assembler.toModel(certificate).getContent();

    assertNotNull(response);
    assertNotNull(response.issuedBy());
    assertEquals("staff-001", response.issuedBy().staffId());
    assertEquals("Dr Test", response.issuedBy().fullName());
    assertNotNull(response.issuedBy().unit());
    assertEquals("unit-001", response.issuedBy().unit().unitId());
    assertNotNull(response.issuedBy().unit().careProvider());
    assertEquals("vg-001", response.issuedBy().unit().careProvider().careProviderId());
  }

  @Test
  void toModel_ShouldReturnNullPatientAndStaffWhenAbsent() {
    final var response = assembler.toModel(minimalCertificate("cert-001")).getContent();

    assertNotNull(response);
    assertNull(response.patient());
    assertNull(response.issuedBy());
  }

  @Test
  void toCollectionModel_ShouldIncludeSelfLinkAndAllItems() {
    final var certificates =
        List.of(minimalCertificate("cert-001"), minimalCertificate("cert-002"));

    final var collection = assembler.toCollectionModel(certificates);

    assertTrue(collection.getLink("self").isPresent());
    assertEquals(2, collection.getContent().size());
  }

  private static Certificate minimalCertificate(final String certificateId) {
    return Certificate.builder().certificateId(certificateId).build();
  }
}
