package se.inera.intyg.intygmockservice.domain.navigation.repository;

import java.util.List;
import se.inera.intyg.intygmockservice.domain.navigation.model.LogEntry;

public interface LogEntryNavigationRepository {

  List<LogEntry> findAll();

  List<LogEntry> findByCertificateId(String certificateId);
}
