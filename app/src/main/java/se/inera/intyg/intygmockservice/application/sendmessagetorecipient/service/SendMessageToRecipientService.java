package se.inera.intyg.intygmockservice.application.sendmessagetorecipient.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.converter.SendMessageToRecipientConverter;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.dto.SendMessageToRecipientDTO;
import se.inera.intyg.intygmockservice.domain.behavior.model.MatchContext;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.SendMessageToRecipientPassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.BehaviorRuleRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.SendMessageToRecipientRepository;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.ObjectFactory;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientResponseType;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendMessageToRecipientService {

  private static final JAXBContext JAXB_CONTEXT;

  static {
    try {
      JAXB_CONTEXT =
          JAXBContext.newInstance(
              "se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2"
                  + ":se.riv.clinicalprocess.healthcond.certificate.v3"
                  + ":se.riv.clinicalprocess.healthcond.certificate.types.v3"
                  + ":org.w3._2000._09.xmldsig_"
                  + ":org.w3._2002._06.xmldsig_filter2");
    } catch (JAXBException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private final SendMessageToRecipientRepository repository;
  private final SendMessageToRecipientConverter converter;
  private final SendMessageToRecipientPassthroughClient passthroughClient;
  private final BehaviorRuleRepository behaviorRuleRepository;
  private final SendMessageToRecipientResponseFactory responseFactory;

  public Optional<SendMessageToRecipientResponseType> store(
      final String logicalAddress, final SendMessageToRecipientType message) {
    final var dto = converter.convert(message);

    final var context =
        MatchContext.builder()
            .logicalAddress(logicalAddress)
            .certificateId(dto.getIntygsId().getExtension())
            .personId(dto.getPatientPersonId().getExtension())
            .build();

    final var ruleOpt =
        behaviorRuleRepository.findBestMatch(ServiceName.SEND_MESSAGE_TO_RECIPIENT, context);

    if (ruleOpt.isPresent()) {
      final var resultOpt = ruleOpt.get().evaluate(context);
      if (resultOpt.isPresent()) {
        return Optional.of(responseFactory.create(resultOpt.get()));
      }
    }

    repository.add(logicalAddress, message);

    log.atInfo()
        .setMessage(
            "Message '%s' on certificate '%s' sent to '%s' with content '%s'"
                .formatted(
                    dto.getMeddelandeId(),
                    dto.getIntygsId().getExtension(),
                    logicalAddress,
                    dto.getMeddelande()))
        .addKeyValue("event.logical_address", logicalAddress)
        .addKeyValue("event.certificate.id", dto.getIntygsId().getExtension())
        .addKeyValue("event.message.id", dto.getMeddelandeId())
        .log();

    return passthroughClient.forward(logicalAddress, message);
  }

  public List<SendMessageToRecipientDTO> getAll() {
    return repository.findAll().stream().map(converter::convert).toList();
  }

  public Optional<SendMessageToRecipientDTO> getByMessageId(final String messageId) {
    return repository.findByMessageId(messageId).map(converter::convert);
  }

  public Optional<String> getAsXml(final String messageId) {
    return repository.findByMessageId(messageId).map(this::marshalToXml);
  }

  public void deleteByMessageId(final String messageId) {
    repository.deleteByMessageId(messageId);
  }

  public List<SendMessageToRecipientDTO> getByCertificateId(final String certificateId) {
    return repository.findByCertificateId(certificateId).stream().map(converter::convert).toList();
  }

  public List<SendMessageToRecipientDTO> getByPersonId(final String personId) {
    final var normalized = normalizePersonId(personId);
    return repository.findByPersonId(normalized).stream().map(converter::convert).toList();
  }

  public List<SendMessageToRecipientDTO> getByLogicalAddress(final String logicalAddress) {
    return repository.findByLogicalAddress(logicalAddress).stream()
        .map(converter::convert)
        .toList();
  }

  public List<SendMessageToRecipientDTO> getByRecipientId(final String recipientId) {
    return repository.findByRecipientId(recipientId).stream().map(converter::convert).toList();
  }

  public int getCount() {
    return repository.count();
  }

  public void deleteAll() {
    repository.deleteAll();
  }

  private static String normalizePersonId(final String personId) {
    return personId == null ? null : personId.replace("-", "");
  }

  private String marshalToXml(final SendMessageToRecipientType type) {
    try {
      final var marshaller = JAXB_CONTEXT.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      final var element = new ObjectFactory().createSendMessageToRecipient(type);
      final var sw = new StringWriter();
      marshaller.marshal(element, sw);
      return sw.toString();
    } catch (JAXBException e) {
      throw new IllegalStateException("Failed to marshal message to XML", e);
    }
  }
}
