package se.inera.intyg.intygmockservice.application.navigation.logentry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.domain.navigation.model.LogEntry;
import se.inera.intyg.intygmockservice.domain.navigation.repository.LogEntryNavigationRepository;

@ExtendWith(MockitoExtension.class)
class LogEntryNavigationServiceTest {

  @Mock private LogEntryNavigationRepository logEntryNavigationRepository;

  @InjectMocks private LogEntryNavigationService service;

  @Test
  void findAll_ShouldDelegateToRepository() {
    final var entry = LogEntry.builder().logId("it-log-001").certificateId("cert-001").build();
    when(logEntryNavigationRepository.findAll()).thenReturn(List.of(entry));

    final var result = service.findAll();

    assertEquals(1, result.size());
    assertEquals("it-log-001", result.get(0).getLogId());
  }

  @Test
  void findAll_ShouldReturnEmptyWhenNoData() {
    when(logEntryNavigationRepository.findAll()).thenReturn(List.of());

    assertTrue(service.findAll().isEmpty());
  }

  @Test
  void findByCertificateId_ShouldDelegateToRepository() {
    final var entry = LogEntry.builder().logId("it-log-001").certificateId("cert-001").build();
    when(logEntryNavigationRepository.findByCertificateId("cert-001")).thenReturn(List.of(entry));

    final var result = service.findByCertificateId("cert-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findByCertificateId_ShouldReturnEmptyWhenNoMatches() {
    when(logEntryNavigationRepository.findByCertificateId("unknown")).thenReturn(List.of());

    assertTrue(service.findByCertificateId("unknown").isEmpty());
  }
}
