package se.inera.intyg.intygmockservice.application.navigation.logentry;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.domain.navigation.model.AuditLogEntry;
import se.inera.intyg.intygmockservice.domain.navigation.repository.AuditLogEntryNavigationRepository;

@Service
@RequiredArgsConstructor
public class LogEntryNavigationService {

  private final AuditLogEntryNavigationRepository auditLogEntryNavigationRepository;

  public List<AuditLogEntry> findAll() {
    return auditLogEntryNavigationRepository.findAll();
  }

  public Optional<AuditLogEntry> findById(final String logId) {
    return auditLogEntryNavigationRepository.findById(logId);
  }

  public List<AuditLogEntry> findByCertificateId(final String certificateId) {
    return auditLogEntryNavigationRepository.findByCertificateId(certificateId);
  }
}
