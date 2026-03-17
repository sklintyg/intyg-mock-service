package se.inera.intyg.intygmockservice.sendmessagetorecipient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.dto.SendMessageToRecipientDTO;

@RestController
@RequestMapping("/api/send-message-to-recipient")
@RequiredArgsConstructor
@Tag(name = "SendMessageToRecipient", description = "APIs for sending messages to recipients")
public class SendMessageToRecipientController {

  private final SendMessageToRecipientService service;

  @GetMapping
  @Operation(summary = "Get all messages", description = "Retrieve all messages")
  public List<SendMessageToRecipientDTO> getAllMessages() {
    return service.getAll();
  }

  @DeleteMapping
  @Operation(summary = "Delete all messages", description = "Delete all messages")
  public void deleteAllMessages() {
    service.deleteAll();
  }

  @GetMapping("/{messageId}")
  @Operation(
      summary = "Get message by message ID",
      description = "Retrieve a single message by its message ID")
  public ResponseEntity<SendMessageToRecipientDTO> getMessageByMessageId(
      @PathVariable final String messageId) {
    return service
        .getByMessageId(messageId)
        .map(ResponseEntity::ok)
        .orElseGet(ResponseEntity.notFound()::build);
  }

  @DeleteMapping("/{messageId}")
  @Operation(
      summary = "Delete message by message ID",
      description = "Delete a single message by its message ID")
  public void deleteMessageByMessageId(@PathVariable final String messageId) {
    service.deleteByMessageId(messageId);
  }

  @GetMapping("/certificate/{certificateId}")
  @Operation(
      summary = "Get messages by certificate ID",
      description = "Retrieve messages for a specific certificate ID")
  public List<SendMessageToRecipientDTO> getMessagesByCertificateId(
      @PathVariable final String certificateId) {
    return service.getByCertificateId(certificateId);
  }

  @GetMapping("/person/{personId}")
  @Operation(
      summary = "Get messages by person ID",
      description = "Retrieve messages for a specific person ID (hyphens ignored)")
  public List<SendMessageToRecipientDTO> getMessagesByPersonId(
      @PathVariable final String personId) {
    return service.getByPersonId(personId);
  }

  @GetMapping("/logical-address/{logicalAddress}")
  @Operation(
      summary = "Get messages by logical address",
      description = "Retrieve messages for a specific logical address")
  public List<SendMessageToRecipientDTO> getMessagesByLogicalAddress(
      @PathVariable final String logicalAddress) {
    return service.getByLogicalAddress(logicalAddress);
  }

  @GetMapping("/recipient/{recipientId}")
  @Operation(
      summary = "Get messages by recipient ID",
      description = "Retrieve messages for a specific recipient ID")
  public List<SendMessageToRecipientDTO> getMessagesByRecipientId(
      @PathVariable final String recipientId) {
    return service.getByRecipientId(recipientId);
  }
}
