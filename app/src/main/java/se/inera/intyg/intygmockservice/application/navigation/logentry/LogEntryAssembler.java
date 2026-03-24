package se.inera.intyg.intygmockservice.application.navigation.logentry;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.domain.navigation.model.LogEntry;

@Component
public class LogEntryAssembler {

  public EntityModel<LogEntryResponse> toModel(final LogEntry logEntry) {
    final var response = toResponse(logEntry);

    final var self =
        logEntry.getLogId() != null
            ? linkTo(methodOn(LogEntryController.class).getLogEntryById(logEntry.getLogId()))
                .withSelfRel()
            : linkTo(methodOn(LogEntryController.class).getAllLogEntries()).withSelfRel();
    final var model = EntityModel.of(response, self);

    if (logEntry.getLogId() != null) {
      model.add(Link.of("/api/store-log/" + logEntry.getLogId() + "/xml", "xml"));
    }

    if (logEntry.getCertificateId() != null) {
      model.add(
          Link.of("/api/navigate/certificates/" + logEntry.getCertificateId(), "certificate"));
    }

    return model;
  }

  public CollectionModel<EntityModel<LogEntryResponse>> toCollectionModel(
      final List<LogEntry> logEntries) {
    final var items = logEntries.stream().map(this::toModel).toList();
    final var selfLink =
        linkTo(methodOn(LogEntryController.class).getAllLogEntries()).withSelfRel();
    return CollectionModel.of(items, selfLink);
  }

  private LogEntryResponse toResponse(final LogEntry logEntry) {
    return new LogEntryResponse(
        logEntry.getLogId(),
        logEntry.getSystemId(),
        logEntry.getSystemName(),
        logEntry.getActivityType(),
        logEntry.getCertificateId(),
        logEntry.getPurpose(),
        logEntry.getActivityStart(),
        logEntry.getUserId(),
        logEntry.getUserAssignment(),
        logEntry.getCareUnitId(),
        logEntry.getCareProviderName());
  }
}
