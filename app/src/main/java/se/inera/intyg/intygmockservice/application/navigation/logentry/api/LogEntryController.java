package se.inera.intyg.intygmockservice.application.navigation.logentry;

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

@RestController
@RequestMapping("/api/navigate")
@RequiredArgsConstructor
@Tag(name = "Navigate — Log Entries", description = "HATEOAS navigation API for log entries")
public class LogEntryController {

  private final LogEntryNavigationService service;
  private final LogEntryAssembler assembler;

  @Operation(summary = "List all log entries")
  @GetMapping("/log-entries")
  public CollectionModel<EntityModel<LogEntryResponse>> getAllLogEntries() {
    return assembler.toCollectionModel(service.findAll());
  }

  @Operation(summary = "Get a log entry by ID")
  @GetMapping("/log-entries/{logId}")
  public ResponseEntity<EntityModel<LogEntryResponse>> getLogEntryById(
      @PathVariable final String logId) {
    return service
        .findById(logId)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
