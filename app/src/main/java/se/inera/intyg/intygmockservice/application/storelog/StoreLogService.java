package se.inera.intyg.intygmockservice.storelog;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.storelog.converter.StoreLogTypeConverter;
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO;
import se.inera.intyg.intygmockservice.storelog.passthrough.StoreLogPassthroughClient;
import se.inera.intyg.intygmockservice.storelog.repository.StoreLogTypeRepository;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogResponseType;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreLogService {

  private final StoreLogTypeRepository repository;
  private final StoreLogTypeConverter converter;
  private final StoreLogPassthroughClient passthroughClient;

  public Optional<StoreLogResponseType> store(
      final String logicalAddress, final StoreLogType storeLogType) {
    repository.add(logicalAddress, storeLogType);

    log.atInfo()
        .setMessage(
            "Stored log received for logical address '%s' with '%s' logs"
                .formatted(logicalAddress, storeLogType.getLog().size()))
        .addKeyValue("event.logical_address", logicalAddress)
        .log();

    return passthroughClient.forward(logicalAddress, storeLogType);
  }

  public List<LogTypeDTO> getAll() {
    final var logs =
        repository.findAll().stream()
            .map(converter::convertToLogTypeDTO)
            .flatMap(List::stream)
            .toList();

    log.atInfo()
        .setMessage("Retrieving all store logs, found '%s' entries".formatted(logs.size()))
        .addKeyValue("event.type", "store-log.get-all")
        .log();

    return logs;
  }

  public List<LogTypeDTO> getByUserId(String userId) {
    final var logs =
        repository.findAll().stream()
            .map(converter::convertToLogTypeDTO)
            .flatMap(List::stream)
            .filter(dto -> userId.equals(dto.getUser().getUserId()))
            .toList();

    log.atInfo()
        .setMessage(
            "Retrieving store logs for user '%s', found '%s' entries"
                .formatted(userId, logs.size()))
        .addKeyValue("event.type", "store-log.get-by-user")
        .addKeyValue("event.user.id", userId)
        .log();

    return logs;
  }

  public List<LogTypeDTO> getByCertificateId(String certificateId) {
    final var logs =
        repository.findAll().stream()
            .map(converter::convertToLogTypeDTO)
            .flatMap(List::stream)
            .filter(dto -> certificateId.equals(dto.getActivity().getActivityLevel()))
            .toList();

    log.atInfo()
        .setMessage(
            "Retrieving store logs for certificate '%s', found '%s' entries"
                .formatted(certificateId, logs.size()))
        .addKeyValue("event.type", "store-log.get-by-certificate")
        .addKeyValue("event.certificate.id", certificateId)
        .log();

    return logs;
  }

  public int getCount() {
    return repository.count();
  }

  public void deleteAll() {
    log.atInfo()
        .setMessage("Deleting all store logs")
        .addKeyValue("event.type", "store-log.delete-all")
        .log();

    repository.deleteAll();
  }

  public void deleteByUserId(String userId) {
    log.atInfo()
        .setMessage("Deleting store logs for user '%s'".formatted(userId))
        .addKeyValue("event.type", "store-log.delete-by-user")
        .addKeyValue("event.user.id", userId)
        .log();

    repository.removeIf(
        storeLogType ->
            storeLogType.getLog().stream().anyMatch(l -> userId.equals(l.getUser().getUserId())));
  }

  public void deleteByCertificateId(String certificateId) {
    log.atInfo()
        .setMessage("Deleting store logs for certificate '%s'".formatted(certificateId))
        .addKeyValue("event.type", "store-log.delete-by-certificate")
        .addKeyValue("event.certificate.id", certificateId)
        .log();

    repository.removeIf(
        storeLogType ->
            storeLogType.getLog().stream()
                .anyMatch(l -> certificateId.equals(l.getActivity().getActivityLevel())));
  }
}
