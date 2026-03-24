package se.inera.intyg.intygmockservice.application.navigation.logentry;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import se.inera.intyg.intygmockservice.domain.navigation.model.LogEntry;

@ExtendWith(MockitoExtension.class)
class LogEntryControllerTest {

  @Mock private LogEntryNavigationService service;
  @Mock private LogEntryAssembler assembler;

  @InjectMocks private LogEntryController controller;

  @Test
  void getAllLogEntries_ShouldReturnCollectionFromAssembler() {
    final var entries = List.of(LogEntry.builder().logId("it-log-001").build());
    final var expected = CollectionModel.<EntityModel<LogEntryResponse>>empty();

    when(service.findAll()).thenReturn(entries);
    when(assembler.toCollectionModel(entries)).thenReturn(expected);

    assertEquals(expected, controller.getAllLogEntries());
  }

  @Test
  void getLogEntryById_ShouldReturn200WithModelWhenFound() {
    final var entry = LogEntry.builder().logId("it-log-001").build();
    final var model = EntityModel.of(new LogEntryResponse("it-log-001", null, null, null, null, null, null, null, null, null, null));

    when(service.findById("it-log-001")).thenReturn(Optional.of(entry));
    when(assembler.toModel(entry)).thenReturn(model);

    final var response = controller.getLogEntryById("it-log-001");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(model, response.getBody());
  }

  @Test
  void getLogEntryById_ShouldReturn404WhenNotFound() {
    when(service.findById("unknown")).thenReturn(Optional.empty());

    final var response = controller.getLogEntryById("unknown");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
