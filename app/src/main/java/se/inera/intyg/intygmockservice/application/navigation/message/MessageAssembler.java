package se.inera.intyg.intygmockservice.application.navigation.message;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.application.navigation.message.api.MessageController;
import se.inera.intyg.intygmockservice.domain.navigation.model.Message;

@Component
public class MessageAssembler {

  public EntityModel<MessageResponse> toModel(final Message message) {
    final var response = toResponse(message);
    final var id = message.getMessageId();

    final var self = linkTo(methodOn(MessageController.class).getMessageById(id)).withSelfRel();

    final var model = EntityModel.of(response, self);

    if (id != null) {
      model.add(Link.of("/api/send-message-to-recipient/" + id + "/xml", "xml"));
    }

    if (message.getCertificateId() != null) {
      model.add(Link.of("/api/navigate/certificates/" + message.getCertificateId(), "certificate"));
    }

    if (message.getPersonId() != null) {
      model.add(Link.of("/api/navigate/patients/" + message.getPersonId().normalized(), "patient"));
    }

    return model;
  }

  public CollectionModel<EntityModel<MessageResponse>> toCollectionModel(
      final List<Message> messages) {
    final var items = messages.stream().map(this::toModel).toList();
    final var selfLink = linkTo(methodOn(MessageController.class).getAllMessages()).withSelfRel();
    return CollectionModel.of(items, selfLink);
  }

  private MessageResponse toResponse(final Message message) {
    return new MessageResponse(
        message.getMessageId(),
        message.getCertificateId(),
        message.getPersonId() != null ? message.getPersonId().normalized() : null,
        message.getRecipient(),
        message.getSubject(),
        message.getHeading(),
        message.getBody(),
        message.getSentTimestamp(),
        message.getSentByStaffId(),
        message.getSentByFullName());
  }
}
