package se.inera.intyg.intygmockservice.application.navigation.logentry;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.domain.navigation.model.LogEntry;
import se.inera.intyg.intygmockservice.domain.navigation.repository.LogEntryNavigationRepository;

@Service
@RequiredArgsConstructor
public class LogEntryNavigationService {

  private final LogEntryNavigationRepository logEntryNavigationRepository;

  public List<LogEntry> findAll() {
    return logEntryNavigationRepository.findAll();
  }

  public Optional<LogEntry> findById(final String logId) {
    return logEntryNavigationRepository.findById(logId);
  }

  public List<LogEntry> findByCertificateId(final String certificateId) {
    return logEntryNavigationRepository.findByCertificateId(certificateId);
  }
}
