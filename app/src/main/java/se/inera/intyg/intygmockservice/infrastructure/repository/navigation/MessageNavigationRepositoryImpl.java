package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.domain.navigation.model.Message;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.repository.MessageNavigationRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.SendMessageToRecipientRepository;
import se.inera.intyg.intygmockservice.infrastructure.xml.JaxbXmlMarshaller;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;

@Repository
@RequiredArgsConstructor
public class MessageNavigationRepositoryImpl implements MessageNavigationRepository {

  private final SendMessageToRecipientRepository sendMessageToRecipientRepository;
  private final JaxbXmlMarshaller xmlMarshaller;

  @Override
  public List<Message> findAll() {
    return sendMessageToRecipientRepository.findAll().stream().map(this::toMessage).toList();
  }

  @Override
  public Optional<Message> findById(final String messageId) {
    return sendMessageToRecipientRepository.findByMessageId(messageId).map(this::toMessage);
  }

  @Override
  public List<Message> findByCertificateId(final String certificateId) {
    return sendMessageToRecipientRepository.findByCertificateId(certificateId).stream()
        .map(this::toMessage)
        .toList();
  }

  @Override
  public List<Message> findByPersonId(final PersonId personId) {
    return sendMessageToRecipientRepository.findByPersonId(personId.normalized()).stream()
        .map(this::toMessage)
        .toList();
  }

  private Message toMessage(final SendMessageToRecipientType source) {
    final var skickatAv = source.getSkickatAv();
    final var staffId =
        skickatAv != null && skickatAv.getPersonalId() != null
            ? skickatAv.getPersonalId().getExtension()
            : null;
    final var staffName = skickatAv != null ? skickatAv.getFullstandigtNamn() : null;

    return Message.builder()
        .messageId(source.getMeddelandeId())
        .certificateId(source.getIntygsId() != null ? source.getIntygsId().getExtension() : null)
        .personId(
            source.getPatientPersonId() != null
                ? PersonId.of(source.getPatientPersonId().getExtension())
                : null)
        .recipient(source.getLogiskAdressMottagare())
        .subject(source.getAmne() != null ? source.getAmne().getCode() : null)
        .heading(source.getRubrik())
        .body(source.getMeddelande())
        .sentTimestamp(source.getSkickatTidpunkt())
        .sentByStaffId(staffId)
        .sentByFullName(staffName)
        .sourceXml(xmlMarshaller.marshal(source))
        .build();
  }
}
