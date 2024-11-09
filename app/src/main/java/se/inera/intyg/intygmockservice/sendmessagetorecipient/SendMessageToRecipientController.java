package se.inera.intyg.intygmockservice.sendmessagetorecipient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.converter.SendMessageToRecipientConverter;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.dto.SendMessageToRecipientDTO;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.repository.SendMessageToRecipientRepository;

@RestController
@RequestMapping("/api/send-message-to-recipient")
@RequiredArgsConstructor
@Tag(name = "SendMessageToRecipient", description = "API for managing messages to recipients")
public class SendMessageToRecipientController {

    private final SendMessageToRecipientRepository repository;
    private final SendMessageToRecipientConverter converter;

    @Operation(summary = "Get all messages", description = "Retrieve all messages sent to recipients")
    @GetMapping
    public List<SendMessageToRecipientDTO> getAllMessages() {
        return repository.findAll().stream()
            .map(converter::convert)
            .collect(Collectors.toList());
    }

    @Operation(summary = "Delete all messages", description = "Delete all messages sent to recipients")
    @DeleteMapping
    public void deleteAllMessages() {
        repository.deleteAll();
    }
}