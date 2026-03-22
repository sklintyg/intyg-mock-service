package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.application.common.dto.PatientDTO.PersonId;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.converter.SendMessageToRecipientConverter;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.dto.SendMessageToRecipientDTO;
import se.inera.intyg.intygmockservice.infrastructure.repository.SendMessageToRecipientRepository;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;

@ExtendWith(MockitoExtension.class)
class MessageNavigationRepositoryImplTest {

  @Mock private SendMessageToRecipientRepository sendMessageToRecipientRepository;
  @Mock private SendMessageToRecipientConverter sendMessageToRecipientConverter;

  @InjectMocks private MessageNavigationRepositoryImpl repository;

  private static SendMessageToRecipientType soapMessage() {
    return new SendMessageToRecipientType();
  }

  private static SendMessageToRecipientDTO dto(
      final String messageId, final String certificateId, final String personId) {
    return SendMessageToRecipientDTO.builder()
        .meddelandeId(messageId)
        .intygsId(IntygsId.builder().extension(certificateId).build())
        .patientPersonId(PersonId.builder().extension(personId).build())
        .build();
  }

  @Test
  void findAll_ShouldReturnAllMessages() {
    final var soap = soapMessage();
    final var dto = dto("msg-001", "cert-001", "191212121212");

    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of(soap));
    when(sendMessageToRecipientConverter.convert(soap)).thenReturn(dto);

    final var result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals("msg-001", result.get(0).getMessageId());
    assertEquals("cert-001", result.get(0).getCertificateId());
    assertEquals("191212121212", result.get(0).getPersonId());
  }

  @Test
  void findAll_ShouldReturnEmptyWhenNoMessages() {
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());

    final var result = repository.findAll();

    assertTrue(result.isEmpty());
  }

  @Test
  void findById_ShouldReturnMessageWhenFound() {
    final var soap = soapMessage();
    final var dto = dto("msg-001", "cert-001", "191212121212");

    when(sendMessageToRecipientRepository.findByMessageId("msg-001")).thenReturn(Optional.of(soap));
    when(sendMessageToRecipientConverter.convert(soap)).thenReturn(dto);

    final var result = repository.findById("msg-001");

    assertTrue(result.isPresent());
    assertEquals("msg-001", result.get().getMessageId());
  }

  @Test
  void findById_ShouldReturnEmptyWhenNotFound() {
    when(sendMessageToRecipientRepository.findByMessageId("unknown")).thenReturn(Optional.empty());

    final var result = repository.findById("unknown");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByCertificateId_ShouldReturnMatchingMessages() {
    final var soap = soapMessage();
    final var dto = dto("msg-001", "cert-001", "191212121212");

    when(sendMessageToRecipientRepository.findByCertificateId("cert-001"))
        .thenReturn(List.of(soap));
    when(sendMessageToRecipientConverter.convert(soap)).thenReturn(dto);

    final var result = repository.findByCertificateId("cert-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findByPersonId_ShouldNormalizeAndReturnMessages() {
    final var soap = soapMessage();
    final var dto = dto("msg-001", "cert-001", "191212121212");

    when(sendMessageToRecipientRepository.findByPersonId("191212121212")).thenReturn(List.of(soap));
    when(sendMessageToRecipientConverter.convert(soap)).thenReturn(dto);

    final var result = repository.findByPersonId("191212121212");

    assertEquals(1, result.size());
    assertEquals("191212121212", result.get(0).getPersonId());
  }
}
