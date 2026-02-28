package se.inera.intyg.intygmockservice.sendmessagetorecipient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.converter.SendMessageToRecipientConverter;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.dto.SendMessageToRecipientDTO;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.repository.SendMessageToRecipientRepository;

@RestController
@RequestMapping("/api/send-message-to-recipient")
@RequiredArgsConstructor
@Tag(name = "SendMessageToRecipient", description = "APIs for sending messages to recipients")
public class SendMessageToRecipientController {

  private final SendMessageToRecipientRepository repository;
  private final SendMessageToRecipientConverter converter;

  @GetMapping
  @Operation(summary = "Get all messages", description = "Retrieve all messages")
  public List<SendMessageToRecipientDTO> getAllMessages() {
    return repository.findAll().stream().map(converter::convert).toList();
  }

  @DeleteMapping
  @Operation(summary = "Delete all messages", description = "Delete all messages")
  public void deleteAllMessages() {
    repository.deleteAll();
  }

  @GetMapping("/recipient/{recipientId}")
  @Operation(
      summary = "Get messages by recipient ID",
      description = "Retrieve messages for a specific recipient ID")
  public List<SendMessageToRecipientDTO> getMessagesByRecipientId(
      @PathVariable String recipientId) {
    return repository.findByRecipientId(recipientId).stream().map(converter::convert).toList();
  }
}
