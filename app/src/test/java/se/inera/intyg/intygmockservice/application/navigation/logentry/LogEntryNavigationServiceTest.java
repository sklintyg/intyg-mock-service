package se.inera.intyg.intygmockservice.application.navigation.logentry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.domain.navigation.model.AuditLogEntry;
import se.inera.intyg.intygmockservice.domain.navigation.repository.AuditLogEntryNavigationRepository;

@ExtendWith(MockitoExtension.class)
class LogEntryNavigationServiceTest {

  @Mock private AuditLogEntryNavigationRepository auditLogEntryNavigationRepository;

  @InjectMocks private LogEntryNavigationService service;

  @Test
  void findAll_ShouldDelegateToRepository() {
    final var entry = AuditLogEntry.builder().logId("it-log-001").certificateId("cert-001").build();
    when(auditLogEntryNavigationRepository.findAll()).thenReturn(List.of(entry));

    final var result = service.findAll();

    assertEquals(1, result.size());
    assertEquals("it-log-001", result.get(0).getLogId());
  }

  @Test
  void findAll_ShouldReturnEmptyWhenNoData() {
    when(auditLogEntryNavigationRepository.findAll()).thenReturn(List.of());

    assertTrue(service.findAll().isEmpty());
  }

  @Test
  void findByCertificateId_ShouldDelegateToRepository() {
    final var entry = AuditLogEntry.builder().logId("it-log-001").certificateId("cert-001").build();
    when(auditLogEntryNavigationRepository.findByCertificateId("cert-001")).thenReturn(List.of(entry));

    final var result = service.findByCertificateId("cert-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findById_ShouldDelegateToRepository() {
    final var entry = AuditLogEntry.builder().logId("it-log-001").certificateId("cert-001").build();
    when(auditLogEntryNavigationRepository.findById("it-log-001")).thenReturn(Optional.of(entry));

    final var result = service.findById("it-log-001");

    assertTrue(result.isPresent());
    assertEquals("it-log-001", result.get().getLogId());
  }

  @Test
  void findById_ShouldReturnEmptyWhenNotFound() {
    when(auditLogEntryNavigationRepository.findById("unknown")).thenReturn(Optional.empty());

    assertTrue(service.findById("unknown").isEmpty());
  }

  @Test
  void findByCertificateId_ShouldReturnEmptyWhenNoMatches() {
    when(auditLogEntryNavigationRepository.findByCertificateId("unknown")).thenReturn(List.of());

    assertTrue(service.findByCertificateId("unknown").isEmpty());
  }
}
