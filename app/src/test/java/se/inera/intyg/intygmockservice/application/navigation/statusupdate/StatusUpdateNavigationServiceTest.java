package se.inera.intyg.intygmockservice.application.navigation.statusupdate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.model.StatusUpdate;
import se.inera.intyg.intygmockservice.domain.navigation.repository.StatusUpdateNavigationRepository;

@ExtendWith(MockitoExtension.class)
class StatusUpdateNavigationServiceTest {

  @Mock private StatusUpdateNavigationRepository statusUpdateNavigationRepository;

  @InjectMocks private StatusUpdateNavigationService service;

  @Test
  void findAll_ShouldDelegateToRepository() {
    final var update = StatusUpdate.builder().certificateId("cert-001").build();
    when(statusUpdateNavigationRepository.findAll()).thenReturn(List.of(update));

    final var result = service.findAll();

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findAll_ShouldReturnEmptyWhenNoData() {
    when(statusUpdateNavigationRepository.findAll()).thenReturn(List.of());

    assertTrue(service.findAll().isEmpty());
  }

  @Test
  void findByCertificateId_ShouldDelegateToRepository() {
    final var update = StatusUpdate.builder().certificateId("cert-001").build();
    when(statusUpdateNavigationRepository.findByCertificateId("cert-001"))
        .thenReturn(List.of(update));

    final var result = service.findByCertificateId("cert-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findByCertificateId_ShouldReturnEmptyWhenNoMatches() {
    when(statusUpdateNavigationRepository.findByCertificateId("unknown")).thenReturn(List.of());

    assertTrue(service.findByCertificateId("unknown").isEmpty());
  }

  @Test
  void findByPersonId_ShouldDelegateToRepository() {
    final var update = StatusUpdate.builder().personId(PersonId.of("191212121212")).build();
    when(statusUpdateNavigationRepository.findByPersonId(PersonId.of("191212121212")))
        .thenReturn(List.of(update));

    final var result = service.findByPersonId("191212121212");

    assertEquals(1, result.size());
    assertEquals(PersonId.of("191212121212"), result.get(0).getPersonId());
  }

  @Test
  void findByPersonId_ShouldReturnEmptyWhenNoMatches() {
    when(statusUpdateNavigationRepository.findByPersonId(PersonId.of("191212121212")))
        .thenReturn(List.of());

    assertTrue(service.findByPersonId("191212121212").isEmpty());
  }
}
