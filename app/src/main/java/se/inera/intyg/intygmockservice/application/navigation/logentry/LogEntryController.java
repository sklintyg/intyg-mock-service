package se.inera.intyg.intygmockservice.application.navigation.logentry;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
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
}
