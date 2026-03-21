package se.inera.intyg.intygmockservice.application.sendmessagetorecipient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.dto.SendMessageToRecipientDTO;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.service.SendMessageToRecipientService;

@ExtendWith(MockitoExtension.class)
class SendMessageToRecipientControllerTest {

  private static final String MESSAGE_ID = "msg-123";
  private static final String CERTIFICATE_ID = "cert-123";
  private static final String PERSON_ID = "191212121212";
  private static final String LOGICAL_ADDRESS = "FK";
  private static final String RECIPIENT_ID = "recipient1";

  @Mock private SendMessageToRecipientService service;

  @InjectMocks private SendMessageToRecipientController controller;

  @Test
  void getAllMessages_ShouldReturnAllMessages() {
    final var dto = SendMessageToRecipientDTO.builder().build();
    when(service.getAll()).thenReturn(List.of(dto));

    final var result = controller.getAllMessages();

    assertThat(result).containsExactly(dto);
  }

  @Test
  void deleteAllMessages_ShouldDeleteAllMessages() {
    controller.deleteAllMessages();

    verify(service).deleteAll();
  }

  @Test
  void getMessagesByRecipientId_ShouldReturnMessagesForRecipient() {
    final var dto = SendMessageToRecipientDTO.builder().build();
    when(service.getByRecipientId(RECIPIENT_ID)).thenReturn(List.of(dto));

    final var result = controller.getMessagesByRecipientId(RECIPIENT_ID);

    assertThat(result).containsExactly(dto);
  }

  @Test
  void getMessageByMessageId_ShouldReturn200WhenFound() {
    final var dto = SendMessageToRecipientDTO.builder().build();
    when(service.getByMessageId(MESSAGE_ID)).thenReturn(Optional.of(dto));

    final var response = controller.getMessageByMessageId(MESSAGE_ID);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto, response.getBody());
  }

  @Test
  void getMessageByMessageId_ShouldReturn404WhenNotFound() {
    when(service.getByMessageId(MESSAGE_ID)).thenReturn(Optional.empty());

    final var response = controller.getMessageByMessageId(MESSAGE_ID);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void deleteMessageByMessageId_ShouldDelegateToService() {
    controller.deleteMessageByMessageId(MESSAGE_ID);

    verify(service).deleteByMessageId(MESSAGE_ID);
  }

  @Test
  void getMessagesByCertificateId_ShouldReturnMessages() {
    final var dto = SendMessageToRecipientDTO.builder().build();
    when(service.getByCertificateId(CERTIFICATE_ID)).thenReturn(List.of(dto));

    final var result = controller.getMessagesByCertificateId(CERTIFICATE_ID);

    assertThat(result).containsExactly(dto);
  }

  @Test
  void getMessagesByPersonId_ShouldReturnMessages() {
    final var dto = SendMessageToRecipientDTO.builder().build();
    when(service.getByPersonId(PERSON_ID)).thenReturn(List.of(dto));

    final var result = controller.getMessagesByPersonId(PERSON_ID);

    assertThat(result).containsExactly(dto);
  }

  @Test
  void getMessagesByLogicalAddress_ShouldReturnMessages() {
    final var dto = SendMessageToRecipientDTO.builder().build();
    when(service.getByLogicalAddress(LOGICAL_ADDRESS)).thenReturn(List.of(dto));

    final var result = controller.getMessagesByLogicalAddress(LOGICAL_ADDRESS);

    assertThat(result).containsExactly(dto);
  }
}
