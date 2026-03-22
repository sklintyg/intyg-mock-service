package se.inera.intyg.intygmockservice.application.navigation.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;

class PatientAssemblerTest {

  private final PatientAssembler assembler = new PatientAssembler();

  @Test
  void toModel_ShouldIncludeSelfLink() {
    final var model = assembler.toModel(patient("191212121212"));

    assertTrue(model.getLink("self").isPresent());
    assertTrue(
        model.getLink("self").get().getHref().contains("/api/navigate/patients/191212121212"));
  }

  @Test
  void toModel_ShouldIncludeCertificatesLink() {
    final var model = assembler.toModel(patient("191212121212"));

    assertTrue(model.getLink("certificates").isPresent());
    assertTrue(
        model
            .getLink("certificates")
            .get()
            .getHref()
            .contains("/api/navigate/patients/191212121212/certificates"));
  }

  @Test
  void toModel_ShouldIncludeMessagesLink() {
    final var model = assembler.toModel(patient("191212121212"));

    assertTrue(model.getLink("messages").isPresent());
    assertTrue(
        model
            .getLink("messages")
            .get()
            .getHref()
            .contains("/api/navigate/patients/191212121212/messages"));
  }

  @Test
  void toModel_ShouldMapAllPatientFields() {
    final var patient =
        Patient.builder()
            .personId("191212121212")
            .firstName("Test")
            .lastName("Testsson")
            .streetAddress("Testgatan 1")
            .postalCode("12345")
            .city("Teststaden")
            .build();

    final var response = assembler.toModel(patient).getContent();

    assertNotNull(response);
    assertEquals("191212121212", response.personId());
    assertEquals("Test", response.firstName());
    assertEquals("Testsson", response.lastName());
    assertEquals("Testgatan 1", response.streetAddress());
    assertEquals("12345", response.postalCode());
    assertEquals("Teststaden", response.city());
  }

  @Test
  void toModel_ShouldReturnNullOptionalFieldsWhenAbsent() {
    final var response = assembler.toModel(patient("191212121212")).getContent();

    assertNotNull(response);
    assertNull(response.firstName());
    assertNull(response.lastName());
  }

  private static Patient patient(final String personId) {
    return Patient.builder().personId(personId).build();
  }
}
