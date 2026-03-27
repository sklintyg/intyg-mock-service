package se.inera.intyg.intygmockservice.domain.navigation.repository;

import java.util.List;
import java.util.Optional;
import se.inera.intyg.intygmockservice.domain.navigation.model.AuditLogEntry;

public interface AuditLogEntryNavigationRepository {

  List<AuditLogEntry> findAll();

  Optional<AuditLogEntry> findById(String logId);

  List<AuditLogEntry> findByCertificateId(String certificateId);
}
