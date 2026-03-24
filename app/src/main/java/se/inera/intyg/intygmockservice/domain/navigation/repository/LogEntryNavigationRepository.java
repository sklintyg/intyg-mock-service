package se.inera.intyg.intygmockservice.domain.navigation.repository;

import java.util.List;
import java.util.Optional;
import se.inera.intyg.intygmockservice.domain.navigation.model.LogEntry;

public interface LogEntryNavigationRepository {

  List<LogEntry> findAll();

  Optional<LogEntry> findById(String logId);

  List<LogEntry> findByCertificateId(String certificateId);
}
