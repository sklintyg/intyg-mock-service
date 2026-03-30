package se.inera.intyg.intygmockservice.application.navigation.message.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.application.navigation.message.MessageAssembler;
import se.inera.intyg.intygmockservice.application.navigation.message.MessageNavigationService;
import se.inera.intyg.intygmockservice.application.navigation.message.MessageResponse;

@RestController
@RequestMapping("/api/navigate/messages")
@RequiredArgsConstructor
@Tag(name = "Navigate — Messages", description = "HATEOAS navigation API for messages")
public class MessageController {

  private final MessageNavigationService service;
  private final MessageAssembler assembler;

  @Operation(summary = "List all messages")
  @GetMapping
  public CollectionModel<EntityModel<MessageResponse>> getAllMessages() {
    return assembler.toCollectionModel(service.findAll());
  }

  @Operation(summary = "Get a message by ID")
  @GetMapping("/{messageId}")
  public ResponseEntity<EntityModel<MessageResponse>> getMessageById(
      @PathVariable final String messageId) {
    return service
        .findById(messageId)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
