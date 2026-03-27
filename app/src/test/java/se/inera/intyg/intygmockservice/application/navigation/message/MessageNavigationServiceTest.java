package se.inera.intyg.intygmockservice.application.navigation.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.domain.navigation.model.Message;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.repository.MessageNavigationRepository;

@ExtendWith(MockitoExtension.class)
class MessageNavigationServiceTest {

  @Mock private MessageNavigationRepository messageNavigationRepository;

  @InjectMocks private MessageNavigationService service;

  @Test
  void findAll_ShouldDelegateToRepository() {
    final var message = Message.builder().messageId("msg-001").build();
    when(messageNavigationRepository.findAll()).thenReturn(List.of(message));

    final var result = service.findAll();

    assertEquals(1, result.size());
    verify(messageNavigationRepository).findAll();
  }

  @Test
  void findById_ShouldDelegateToRepository() {
    final var message = Message.builder().messageId("msg-001").build();
    when(messageNavigationRepository.findById("msg-001")).thenReturn(Optional.of(message));

    final var result = service.findById("msg-001");

    assertTrue(result.isPresent());
    assertEquals("msg-001", result.get().getMessageId());
    verify(messageNavigationRepository).findById("msg-001");
  }

  @Test
  void findByCertificateId_ShouldDelegateToRepository() {
    final var message = Message.builder().messageId("msg-001").certificateId("cert-001").build();
    when(messageNavigationRepository.findByCertificateId("cert-001")).thenReturn(List.of(message));

    final var result = service.findByCertificateId("cert-001");

    assertEquals(1, result.size());
    verify(messageNavigationRepository).findByCertificateId("cert-001");
  }

  @Test
  void findByPersonId_ShouldDelegateToRepository() {
    final var message =
        Message.builder().messageId("msg-001").personId(PersonId.of("191212121212")).build();
    when(messageNavigationRepository.findByPersonId(PersonId.of("191212121212")))
        .thenReturn(List.of(message));

    final var result = service.findByPersonId("191212121212");

    assertEquals(1, result.size());
    verify(messageNavigationRepository).findByPersonId(PersonId.of("191212121212"));
  }
}
