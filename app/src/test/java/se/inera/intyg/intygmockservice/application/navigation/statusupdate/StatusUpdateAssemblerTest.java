package se.inera.intyg.intygmockservice.application.navigation.statusupdate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.model.StatusUpdate;

class StatusUpdateAssemblerTest {

  private final StatusUpdateAssembler assembler = new StatusUpdateAssembler();

  @Test
  void toModel_ShouldIncludeSelfLink() {
    final var model = assembler.toModel(statusUpdate("cert-001", "191212121212"));

    assertTrue(model.getLink("self").isPresent());
    assertTrue(
        model
            .getLink("self")
            .get()
            .getHref()
            .contains("/api/navigate/certificates/cert-001/status-updates"));
  }

  @Test
  void toModel_ShouldIncludeCertificateLinkWhenCertificateIdPresent() {
    final var model = assembler.toModel(statusUpdate("cert-001", "191212121212"));

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
    final var update = StatusUpdate.builder().personId(PersonId.of("191212121212")).build();

    assertTrue(assembler.toModel(update).getLink("certificate").isEmpty());
  }

  @Test
  void toModel_ShouldIncludePatientLinkWhenPersonIdPresent() {
    final var model = assembler.toModel(statusUpdate("cert-001", "191212121212"));

    assertTrue(model.getLink("patient").isPresent());
    assertTrue(
        model.getLink("patient").get().getHref().contains("/api/navigate/patients/191212121212"));
  }

  @Test
  void toModel_ShouldNotIncludePatientLinkWhenPersonIdNull() {
    final var update = StatusUpdate.builder().certificateId("cert-001").build();

    assertTrue(assembler.toModel(update).getLink("patient").isEmpty());
  }

  @Test
  void toModel_ShouldMapAllFields() {
    final var timestamp = LocalDateTime.of(2024, 11, 9, 7, 40, 13);
    final var update =
        StatusUpdate.builder()
            .certificateId("cert-001")
            .personId(PersonId.of("191212121212"))
            .eventCode("SKAPAT")
            .eventDisplayName("Intyg skapat")
            .eventTimestamp(timestamp)
            .questionsSentTotal(2)
            .questionsReceivedTotal(1)
            .build();

    final var response = assembler.toModel(update).getContent();

    assertNotNull(response);
    assertEquals("cert-001", response.certificateId());
    assertEquals("191212121212", response.personId());
    assertEquals("SKAPAT", response.eventCode());
    assertEquals("Intyg skapat", response.eventDisplayName());
    assertEquals(timestamp, response.eventTimestamp());
    assertEquals(2, response.questionsSentTotal());
    assertEquals(1, response.questionsReceivedTotal());
  }

  @Test
  void toCollectionModel_ShouldIncludeSelfLink() {
    final var collection =
        assembler.toCollectionModel(List.of(statusUpdate("cert-001", "191212121212")));

    assertTrue(collection.getLink("self").isPresent());
    assertTrue(collection.getLink("self").get().getHref().contains("/api/navigate/status-updates"));
  }

  @Test
  void toCollectionModel_ShouldContainAllItems() {
    final var updates =
        List.of(statusUpdate("cert-001", "191212121212"), statusUpdate("cert-002", "191212121212"));

    assertEquals(2, assembler.toCollectionModel(updates).getContent().size());
  }

  private static StatusUpdate statusUpdate(final String certificateId, final String personId) {
    return StatusUpdate.builder()
        .certificateId(certificateId)
        .personId(PersonId.of(personId))
        .build();
  }
}
