package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.domain.navigation.model.AuditLogEntry;
import se.inera.intyg.intygmockservice.domain.navigation.repository.AuditLogEntryNavigationRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.StoreLogTypeRepository;
import se.inera.intyg.intygmockservice.infrastructure.xml.JaxbXmlMarshaller;
import se.riv.informationsecurity.auditing.log.v2.LogType;

@Repository
@RequiredArgsConstructor
public class AuditLogEntryNavigationRepositoryImpl implements AuditLogEntryNavigationRepository {

  private final StoreLogTypeRepository storeLogTypeRepository;
  private final JaxbXmlMarshaller xmlMarshaller;

  @Override
  public List<AuditLogEntry> findAll() {
    return storeLogTypeRepository.findAll().stream()
        .flatMap(storeLogType -> storeLogType.getLog().stream())
        .map(this::toAuditLogEntry)
        .toList();
  }

  @Override
  public Optional<AuditLogEntry> findById(final String logId) {
    return storeLogTypeRepository.findAll().stream()
        .flatMap(storeLogType -> storeLogType.getLog().stream())
        .filter(log -> logId.equals(log.getLogId()))
        .map(this::toAuditLogEntry)
        .findFirst();
  }

  @Override
  public List<AuditLogEntry> findByCertificateId(final String certificateId) {
    return storeLogTypeRepository.findAll().stream()
        .flatMap(storeLogType -> storeLogType.getLog().stream())
        .filter(
            log ->
                log.getActivity() != null
                    && certificateId.equals(log.getActivity().getActivityLevel()))
        .map(this::toAuditLogEntry)
        .toList();
  }

  private AuditLogEntry toAuditLogEntry(final LogType log) {
    final var system = log.getSystem();
    final var activity = log.getActivity();
    final var user = log.getUser();

    return AuditLogEntry.builder()
        .logId(log.getLogId())
        .systemId(system != null ? system.getSystemId() : null)
        .systemName(system != null ? system.getSystemName() : null)
        .activityType(activity != null ? activity.getActivityType() : null)
        .certificateId(activity != null ? activity.getActivityLevel() : null)
        .purpose(activity != null ? activity.getPurpose() : null)
        .activityStart(activity != null ? activity.getStartDate() : null)
        .userId(user != null ? user.getUserId() : null)
        .userAssignment(user != null ? user.getAssignment() : null)
        .careUnitId(
            user != null && user.getCareUnit() != null ? user.getCareUnit().getCareUnitId() : null)
        .careProviderName(
            user != null && user.getCareProvider() != null
                ? user.getCareProvider().getCareProviderName()
                : null)
        .sourceXml(xmlMarshaller.marshal(log))
        .build();
  }
}
