package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.application.storelog.converter.StoreLogTypeConverter;
import se.inera.intyg.intygmockservice.application.storelog.dto.LogTypeDTO;
import se.inera.intyg.intygmockservice.domain.navigation.model.LogEntry;
import se.inera.intyg.intygmockservice.domain.navigation.repository.LogEntryNavigationRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.StoreLogTypeRepository;

@Repository
@RequiredArgsConstructor
public class LogEntryNavigationRepositoryImpl implements LogEntryNavigationRepository {

  private final StoreLogTypeRepository storeLogTypeRepository;
  private final StoreLogTypeConverter converter;

  @Override
  public List<LogEntry> findAll() {
    return storeLogTypeRepository.findAll().stream()
        .flatMap(storeLogType -> converter.convertToLogTypeDTO(storeLogType).stream())
        .map(this::toLogEntry)
        .toList();
  }

  @Override
  public List<LogEntry> findByCertificateId(final String certificateId) {
    return storeLogTypeRepository.findAll().stream()
        .flatMap(storeLogType -> converter.convertToLogTypeDTO(storeLogType).stream())
        .filter(
            dto ->
                dto.getActivity() != null
                    && certificateId.equals(dto.getActivity().getActivityLevel()))
        .map(this::toLogEntry)
        .toList();
  }

  private LogEntry toLogEntry(final LogTypeDTO dto) {
    final var system = dto.getSystem();
    final var activity = dto.getActivity();
    final var user = dto.getUser();

    return LogEntry.builder()
        .logId(dto.getLogId())
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
        .build();
  }
}
