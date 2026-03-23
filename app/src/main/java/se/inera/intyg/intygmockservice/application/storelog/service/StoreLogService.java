package se.inera.intyg.intygmockservice.application.storelog.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.storelog.converter.StoreLogTypeConverter;
import se.inera.intyg.intygmockservice.application.storelog.dto.LogTypeDTO;
import se.inera.intyg.intygmockservice.domain.behavior.model.MatchContext;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.StoreLogPassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.BehaviorRuleRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.StoreLogTypeRepository;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.ObjectFactory;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogResponseType;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreLogService {

  private static final JAXBContext JAXB_CONTEXT;

  static {
    try {
      JAXB_CONTEXT =
          JAXBContext.newInstance(
              "se.riv.informationsecurity.auditing.log.StoreLogResponder.v2"
                  + ":se.riv.informationsecurity.auditing.log.v2");
    } catch (JAXBException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private final StoreLogTypeRepository repository;
  private final StoreLogTypeConverter converter;
  private final StoreLogPassthroughClient passthroughClient;
  private final BehaviorRuleRepository behaviorRuleRepository;
  private final StoreLogResponseFactory responseFactory;

  public Optional<StoreLogResponseType> store(
      final String logicalAddress, final StoreLogType storeLogType) {
    final var certificateId =
        storeLogType.getLog().isEmpty()
            ? null
            : storeLogType.getLog().get(0).getActivity().getActivityLevel();
    final var context =
        MatchContext.builder().logicalAddress(logicalAddress).certificateId(certificateId).build();
    final var ruleOpt = behaviorRuleRepository.findBestMatch(ServiceName.STORE_LOG, context);
    if (ruleOpt.isPresent()) {
      final var resultOpt = ruleOpt.get().evaluate(context);
      if (resultOpt.isPresent()) {
        return Optional.of(responseFactory.create(resultOpt.get()));
      }
    }

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

  public Optional<String> getAsXml(final String logId) {
    return repository.findAll().stream()
        .filter(
            storeLogType ->
                storeLogType.getLog().stream().anyMatch(l -> logId.equals(l.getLogId())))
        .findFirst()
        .map(this::marshalToXml);
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

  private String marshalToXml(final StoreLogType storeLogType) {
    try {
      final var marshaller = JAXB_CONTEXT.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      final var element = new ObjectFactory().createStoreLog(storeLogType);
      final var sw = new StringWriter();
      marshaller.marshal(element, sw);
      return sw.toString();
    } catch (JAXBException e) {
      throw new IllegalStateException("Failed to marshal store log to XML", e);
    }
  }
}
