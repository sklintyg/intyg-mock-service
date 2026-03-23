package se.inera.intyg.intygmockservice.application.navigation.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import se.inera.intyg.intygmockservice.domain.navigation.model.Message;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

  @Mock private MessageNavigationService service;
  @Mock private MessageAssembler assembler;

  @InjectMocks private MessageController controller;

  @Test
  void getAllMessages_ShouldDelegateToServiceAndAssembler() {
    final var message = Message.builder().messageId("msg-001").build();
    final var collectionModel = CollectionModel.<EntityModel<MessageResponse>>empty();

    when(service.findAll()).thenReturn(List.of(message));
    when(assembler.toCollectionModel(List.of(message))).thenReturn(collectionModel);

    final var result = controller.getAllMessages();

    assertNotNull(result);
    verify(service).findAll();
    verify(assembler).toCollectionModel(List.of(message));
  }

  @Test
  void getMessageById_ShouldReturn200WhenFound() {
    final var message = Message.builder().messageId("msg-001").build();
    final var entityModel =
        EntityModel.of(
            new MessageResponse("msg-001", null, null, null, null, null, null, null, null, null));

    when(service.findById("msg-001")).thenReturn(Optional.of(message));
    when(assembler.toModel(message)).thenReturn(entityModel);

    final var response = controller.getMessageById("msg-001");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void getMessageById_ShouldReturn404WhenNotFound() {
    when(service.findById("unknown")).thenReturn(Optional.empty());

    final var response = controller.getMessageById("unknown");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
