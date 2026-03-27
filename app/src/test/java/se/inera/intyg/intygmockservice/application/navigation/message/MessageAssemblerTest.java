package se.inera.intyg.intygmockservice.application.navigation.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.navigation.model.Message;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;

class MessageAssemblerTest {

  private final MessageAssembler assembler = new MessageAssembler();

  @Test
  void toModel_ShouldIncludeSelfLink() {
    final var model = assembler.toModel(message("msg-001"));

    assertTrue(model.getLink("self").isPresent());
    assertTrue(model.getLink("self").get().getHref().contains("/api/navigate/messages/msg-001"));
  }

  @Test
  void toModel_ShouldIncludeCertificateLinkWhenCertificateIdPresent() {
    final var msg = Message.builder().messageId("msg-001").certificateId("cert-001").build();

    final var model = assembler.toModel(msg);

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
    final var model = assembler.toModel(message("msg-001"));

    assertTrue(model.getLink("certificate").isEmpty());
  }

  @Test
  void toModel_ShouldIncludePatientLinkWhenPersonIdPresent() {
    final var msg =
        Message.builder().messageId("msg-001").personId(PersonId.of("191212121212")).build();

    final var model = assembler.toModel(msg);

    assertTrue(model.getLink("patient").isPresent());
    assertTrue(
        model.getLink("patient").get().getHref().contains("/api/navigate/patients/191212121212"));
  }

  @Test
  void toModel_ShouldNotIncludePatientLinkWhenPersonIdNull() {
    final var model = assembler.toModel(message("msg-001"));

    assertTrue(model.getLink("patient").isEmpty());
  }

  @Test
  void toModel_ShouldMapAllFields() {
    final var sent = LocalDateTime.of(2024, 11, 9, 7, 40, 13);
    final var msg =
        Message.builder()
            .messageId("msg-001")
            .certificateId("cert-001")
            .personId(PersonId.of("191212121212"))
            .recipient("FK")
            .subject("OVRIGT")
            .heading("Test rubrik")
            .body("Test body")
            .sentTimestamp(sent)
            .sentByStaffId("TSTNMT2321000156-DRAA")
            .sentByFullName("Ajla Doktor")
            .build();

    final var response = assembler.toModel(msg).getContent();

    assertNotNull(response);
    assertEquals("msg-001", response.messageId());
    assertEquals("cert-001", response.certificateId());
    assertEquals("191212121212", response.personId());
    assertEquals("FK", response.recipient());
    assertEquals("OVRIGT", response.subject());
    assertEquals("Test rubrik", response.heading());
    assertEquals("Test body", response.body());
    assertEquals(sent, response.sentTimestamp());
    assertEquals("TSTNMT2321000156-DRAA", response.sentByStaffId());
    assertEquals("Ajla Doktor", response.sentByFullName());
  }

  @Test
  void toModel_ShouldReturnNullOptionalFieldsWhenAbsent() {
    final var response = assembler.toModel(message("msg-001")).getContent();

    assertNotNull(response);
    assertNull(response.certificateId());
    assertNull(response.personId());
    assertNull(response.recipient());
  }

  @Test
  void toCollectionModel_ShouldIncludeSelfLink() {
    final var model = assembler.toCollectionModel(List.of(message("msg-001")));

    assertTrue(model.getLink("self").isPresent());
    assertTrue(model.getLink("self").get().getHref().contains("/api/navigate/messages"));
  }

  @Test
  void toCollectionModel_ShouldContainAllItems() {
    final var model = assembler.toCollectionModel(List.of(message("msg-001"), message("msg-002")));

    assertEquals(2, model.getContent().size());
  }

  private static Message message(final String messageId) {
    return Message.builder().messageId(messageId).build();
  }
}
