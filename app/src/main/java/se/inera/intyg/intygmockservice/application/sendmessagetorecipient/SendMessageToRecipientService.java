package se.inera.intyg.intygmockservice.application.sendmessagetorecipient;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.converter.SendMessageToRecipientConverter;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.dto.SendMessageToRecipientDTO;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.SendMessageToRecipientPassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.SendMessageToRecipientRepository;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientResponseType;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendMessageToRecipientService {

  private final SendMessageToRecipientRepository repository;
  private final SendMessageToRecipientConverter converter;
  private final SendMessageToRecipientPassthroughClient passthroughClient;

  public Optional<SendMessageToRecipientResponseType> store(
      final String logicalAddress, final SendMessageToRecipientType message) {
    repository.add(logicalAddress, message);

    final var dto = converter.convert(message);

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
}
