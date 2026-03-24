package se.inera.intyg.intygmockservice.application.navigation.logentry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.navigation.model.LogEntry;

class LogEntryAssemblerTest {

  private final LogEntryAssembler assembler = new LogEntryAssembler();

  @Test
  void toModel_ShouldIncludeSelfLinkPointingToLogEntryByIdWhenLogIdPresent() {
    final var model = assembler.toModel(logEntry("cert-001"));

    assertTrue(model.getLink("self").isPresent());
    assertTrue(
        model.getLink("self").get().getHref().contains("/api/navigate/log-entries/it-log-001"));
  }

  @Test
  void toModel_ShouldIncludeSelfLinkPointingToAllLogEntriesWhenLogIdNull() {
    final var entry = LogEntry.builder().certificateId("cert-001").build();

    assertTrue(
        assembler
            .toModel(entry)
            .getLink("self")
            .get()
            .getHref()
            .contains("/api/navigate/log-entries"));
  }

  @Test
  void toModel_ShouldIncludeCertificateLinkWhenCertificateIdPresent() {
    final var model = assembler.toModel(logEntry("cert-001"));

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
    final var entry = LogEntry.builder().logId("it-log-001").build();

    assertTrue(assembler.toModel(entry).getLink("certificate").isEmpty());
  }

  @Test
  void toModel_ShouldMapAllFields() {
    final var timestamp = LocalDateTime.of(2024, 11, 9, 7, 40, 13);
    final var entry =
        LogEntry.builder()
            .logId("it-log-001")
            .systemId("WEBCERT")
            .systemName("Webcert")
            .activityType("Läsa")
            .certificateId("cert-001")
            .purpose("CARE_TREATMENT")
            .activityStart(timestamp)
            .userId("it-user-001")
            .userAssignment("Läkare")
            .careUnitId("ALMC")
            .careProviderName("Alfa Regionen")
            .build();

    final var response = assembler.toModel(entry).getContent();

    assertNotNull(response);
    assertEquals("it-log-001", response.logId());
    assertEquals("WEBCERT", response.systemId());
    assertEquals("Webcert", response.systemName());
    assertEquals("Läsa", response.activityType());
    assertEquals("cert-001", response.certificateId());
    assertEquals("CARE_TREATMENT", response.purpose());
    assertEquals(timestamp, response.activityStart());
    assertEquals("it-user-001", response.userId());
    assertEquals("Läkare", response.userAssignment());
    assertEquals("ALMC", response.careUnitId());
    assertEquals("Alfa Regionen", response.careProviderName());
  }

  @Test
  void toCollectionModel_ShouldIncludeSelfLink() {
    final var collection = assembler.toCollectionModel(List.of(logEntry("cert-001")));

    assertTrue(collection.getLink("self").isPresent());
    assertTrue(collection.getLink("self").get().getHref().contains("/api/navigate/log-entries"));
  }

  @Test
  void toCollectionModel_ShouldContainAllItems() {
    final var entries = List.of(logEntry("cert-001"), logEntry("cert-002"));

    assertEquals(2, assembler.toCollectionModel(entries).getContent().size());
  }

  private static LogEntry logEntry(final String certificateId) {
    return LogEntry.builder().logId("it-log-001").certificateId(certificateId).build();
  }
}
