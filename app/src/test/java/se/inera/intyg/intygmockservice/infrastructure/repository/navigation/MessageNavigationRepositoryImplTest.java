package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.infrastructure.repository.SendMessageToRecipientRepository;
import se.inera.intyg.intygmockservice.infrastructure.xml.JaxbXmlMarshaller;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.IntygId;

@ExtendWith(MockitoExtension.class)
class MessageNavigationRepositoryImplTest {

  @Mock private SendMessageToRecipientRepository sendMessageToRecipientRepository;
  @Mock private JaxbXmlMarshaller xmlMarshaller;

  @InjectMocks private MessageNavigationRepositoryImpl repository;

  private static SendMessageToRecipientType message(
      final String messageId, final String certificateId, final String personId) {
    final var intygsId = new IntygId();
    intygsId.setExtension(certificateId);

    final var pid = new se.riv.clinicalprocess.healthcond.certificate.types.v3.PersonId();
    pid.setExtension(personId);

    final var msg = new SendMessageToRecipientType();
    msg.setMeddelandeId(messageId);
    msg.setIntygsId(intygsId);
    msg.setPatientPersonId(pid);
    return msg;
  }

  @Test
  void findAll_ShouldReturnAllMessages() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    final var msg = message("msg-001", "cert-001", "191212121212");
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of(msg));

    final var result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals("msg-001", result.get(0).getMessageId());
    assertEquals("cert-001", result.get(0).getCertificateId());
    assertEquals(PersonId.of("191212121212"), result.get(0).getPersonId());
  }

  @Test
  void findAll_ShouldReturnEmptyWhenNoMessages() {
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());

    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  void findById_ShouldReturnMessageWhenFound() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    final var msg = message("msg-001", "cert-001", "191212121212");
    when(sendMessageToRecipientRepository.findByMessageId("msg-001")).thenReturn(Optional.of(msg));

    final var result = repository.findById("msg-001");

    assertTrue(result.isPresent());
    assertEquals("msg-001", result.get().getMessageId());
  }

  @Test
  void findById_ShouldReturnEmptyWhenNotFound() {
    when(sendMessageToRecipientRepository.findByMessageId("unknown")).thenReturn(Optional.empty());

    assertTrue(repository.findById("unknown").isEmpty());
  }

  @Test
  void findByCertificateId_ShouldReturnMatchingMessages() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    final var msg = message("msg-001", "cert-001", "191212121212");
    when(sendMessageToRecipientRepository.findByCertificateId("cert-001")).thenReturn(List.of(msg));

    final var result = repository.findByCertificateId("cert-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findByPersonId_ShouldNormalizeAndReturnMessages() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    final var msg = message("msg-001", "cert-001", "191212121212");
    when(sendMessageToRecipientRepository.findByPersonId("191212121212")).thenReturn(List.of(msg));

    final var result = repository.findByPersonId(PersonId.of("191212121212"));

    assertEquals(1, result.size());
    assertEquals(PersonId.of("191212121212"), result.get(0).getPersonId());
  }
}
