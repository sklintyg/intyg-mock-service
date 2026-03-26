package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.converter.SendMessageToRecipientConverter;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.dto.SendMessageToRecipientDTO;
import se.inera.intyg.intygmockservice.domain.navigation.model.Message;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.repository.MessageNavigationRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.SendMessageToRecipientRepository;

@Repository
@RequiredArgsConstructor
public class MessageNavigationRepositoryImpl implements MessageNavigationRepository {

  private final SendMessageToRecipientRepository sendMessageToRecipientRepository;
  private final SendMessageToRecipientConverter sendMessageToRecipientConverter;

  @Override
  public List<Message> findAll() {
    return sendMessageToRecipientRepository.findAll().stream()
        .map(sendMessageToRecipientConverter::convert)
        .map(this::toMessage)
        .toList();
  }

  @Override
  public Optional<Message> findById(final String messageId) {
    return sendMessageToRecipientRepository
        .findByMessageId(messageId)
        .map(sendMessageToRecipientConverter::convert)
        .map(this::toMessage);
  }

  @Override
  public List<Message> findByCertificateId(final String certificateId) {
    return sendMessageToRecipientRepository.findByCertificateId(certificateId).stream()
        .map(sendMessageToRecipientConverter::convert)
        .map(this::toMessage)
        .toList();
  }

  @Override
  public List<Message> findByPersonId(final PersonId personId) {
    return sendMessageToRecipientRepository.findByPersonId(personId.normalized()).stream()
        .map(sendMessageToRecipientConverter::convert)
        .map(this::toMessage)
        .toList();
  }

  private Message toMessage(final SendMessageToRecipientDTO dto) {
    final var staffId =
        dto.getSkickatAv() != null && dto.getSkickatAv().getPersonalId() != null
            ? dto.getSkickatAv().getPersonalId().getExtension()
            : null;
    final var staffName =
        dto.getSkickatAv() != null ? dto.getSkickatAv().getFullstandigtNamn() : null;

    return Message.builder()
        .messageId(dto.getMeddelandeId())
        .certificateId(dto.getIntygsId() != null ? dto.getIntygsId().getExtension() : null)
        .personId(
            dto.getPatientPersonId() != null
                ? PersonId.of(dto.getPatientPersonId().getExtension())
                : null)
        .recipient(dto.getLogiskAdressMottagare())
        .subject(dto.getAmne() != null ? dto.getAmne().getCode() : null)
        .heading(dto.getRubrik())
        .body(dto.getMeddelande())
        .sentTimestamp(dto.getSkickatTidpunkt())
        .sentByStaffId(staffId)
        .sentByFullName(staffName)
        .build();
  }
}
