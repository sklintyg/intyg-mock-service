package se.inera.intyg.intygmockservice.application.navigation.statusupdate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.domain.navigation.model.StatusUpdate;

@Component
public class StatusUpdateAssembler {

  public EntityModel<StatusUpdateResponse> toModel(final StatusUpdate statusUpdate) {
    final var response = toResponse(statusUpdate);
    final var certId = statusUpdate.getCertificateId();

    final var self =
        linkTo(methodOn(StatusUpdateController.class).getCertificateStatusUpdates(certId))
            .withSelfRel();
    final var model = EntityModel.of(response, self);

    if (certId != null) {
      model.add(Link.of("/api/navigate/certificates/" + certId, "certificate"));
      model.add(Link.of("/api/certificate-status-for-care/" + certId + "/xml", "xml"));
    }

    if (statusUpdate.getPersonId() != null) {
      model.add(
          Link.of("/api/navigate/patients/" + statusUpdate.getPersonId().normalized(), "patient"));
    }

    return model;
  }

  public CollectionModel<EntityModel<StatusUpdateResponse>> toCollectionModel(
      final List<StatusUpdate> statusUpdates) {
    final var items = statusUpdates.stream().map(this::toModel).toList();
    final var selfLink =
        linkTo(methodOn(StatusUpdateController.class).getAllStatusUpdates()).withSelfRel();
    return CollectionModel.of(items, selfLink);
  }

  private StatusUpdateResponse toResponse(final StatusUpdate statusUpdate) {
    return new StatusUpdateResponse(
        statusUpdate.getCertificateId(),
        statusUpdate.getPersonId() != null ? statusUpdate.getPersonId().normalized() : null,
        statusUpdate.getEventCode(),
        statusUpdate.getEventDisplayName(),
        statusUpdate.getEventTimestamp(),
        statusUpdate.getQuestionsSentTotal(),
        statusUpdate.getQuestionsReceivedTotal());
  }
}
