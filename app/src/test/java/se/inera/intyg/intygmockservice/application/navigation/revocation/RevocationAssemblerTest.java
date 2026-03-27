package se.inera.intyg.intygmockservice.application.navigation.revocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.model.Revocation;

class RevocationAssemblerTest {

  private final RevocationAssembler assembler = new RevocationAssembler();

  @Test
  void toModel_ShouldIncludeSelfLink() {
    final var model = assembler.toModel(revocation("cert-001", "191212121212"));

    assertTrue(model.getLink("self").isPresent());
    assertTrue(
        model
            .getLink("self")
            .get()
            .getHref()
            .contains("/api/navigate/certificates/cert-001/revocation"));
  }

  @Test
  void toModel_ShouldIncludeCertificateLinkWhenCertificateIdPresent() {
    final var model = assembler.toModel(revocation("cert-001", "191212121212"));

    assertTrue(model.getLink("certificate").isPresent());
    assertTrue(
        model
            .getLink("certificate")
            .get()
            .getHref()
            .contains("/api/navigate/certificates/cert-001"));
  }

  @Test
  void toModel_ShouldNotIncludeCertificateLinkWhenCertificateIdNull() {
    final var rev = Revocation.builder().personId(PersonId.of("191212121212")).build();

    final var model = assembler.toModel(rev);

    assertTrue(model.getLink("certificate").isEmpty());
  }

  @Test
  void toModel_ShouldIncludePatientLinkWhenPersonIdPresent() {
    final var model = assembler.toModel(revocation("cert-001", "191212121212"));

    assertTrue(model.getLink("patient").isPresent());
    assertTrue(
        model.getLink("patient").get().getHref().contains("/api/navigate/patients/191212121212"));
  }

  @Test
  void toModel_ShouldNotIncludePatientLinkWhenPersonIdNull() {
    final var rev = Revocation.builder().certificateId("cert-001").build();

    final var model = assembler.toModel(rev);

    assertTrue(model.getLink("patient").isEmpty());
  }

  @Test
  void toModel_ShouldMapAllFields() {
    final var revokedAt = LocalDateTime.of(2024, 11, 9, 7, 40, 13);
    final var rev =
        Revocation.builder()
            .certificateId("cert-001")
            .personId(PersonId.of("191212121212"))
            .revokedAt(revokedAt)
            .reason("Felaktig uppgift")
            .revokedByStaffId("TSTNMT2321000156-DRAA")
            .revokedByFullName("Ajla Doktor")
            .build();

    final var response = assembler.toModel(rev).getContent();

    assertNotNull(response);
    assertEquals("cert-001", response.certificateId());
    assertEquals("191212121212", response.personId());
    assertEquals(revokedAt, response.revokedAt());
    assertEquals("Felaktig uppgift", response.reason());
    assertEquals("TSTNMT2321000156-DRAA", response.revokedByStaffId());
    assertEquals("Ajla Doktor", response.revokedByFullName());
  }

  @Test
  void toModel_ShouldReturnNullOptionalFieldsWhenAbsent() {
    final var response =
        assembler.toModel(Revocation.builder().certificateId("cert-001").build()).getContent();

    assertNotNull(response);
    assertNull(response.personId());
    assertNull(response.reason());
    assertNull(response.revokedByStaffId());
  }

  private static Revocation revocation(final String certificateId, final String personId) {
    return Revocation.builder()
        .certificateId(certificateId)
        .personId(PersonId.of(personId))
        .build();
  }
}
