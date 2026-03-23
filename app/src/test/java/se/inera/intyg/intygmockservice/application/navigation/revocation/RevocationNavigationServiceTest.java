package se.inera.intyg.intygmockservice.application.navigation.revocation;

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
import se.inera.intyg.intygmockservice.domain.navigation.model.Revocation;
import se.inera.intyg.intygmockservice.domain.navigation.repository.RevocationNavigationRepository;

@ExtendWith(MockitoExtension.class)
class RevocationNavigationServiceTest {

  @Mock private RevocationNavigationRepository revocationNavigationRepository;

  @InjectMocks private RevocationNavigationService service;

  @Test
  void findByCertificateId_ShouldDelegateToRepository() {
    final var revocation = Revocation.builder().certificateId("cert-001").build();
    when(revocationNavigationRepository.findByCertificateId("cert-001"))
        .thenReturn(Optional.of(revocation));

    final var result = service.findByCertificateId("cert-001");

    assertTrue(result.isPresent());
    assertEquals("cert-001", result.get().getCertificateId());
  }

  @Test
  void findByCertificateId_ShouldReturnEmptyWhenNotFound() {
    when(revocationNavigationRepository.findByCertificateId("unknown"))
        .thenReturn(Optional.empty());

    final var result = service.findByCertificateId("unknown");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByPersonId_ShouldDelegateToRepository() {
    final var revocation = Revocation.builder().personId("191212121212").build();
    when(revocationNavigationRepository.findByPersonId("191212121212"))
        .thenReturn(List.of(revocation));

    final var result = service.findByPersonId("191212121212");

    assertEquals(1, result.size());
    assertEquals("191212121212", result.get(0).getPersonId());
  }

  @Test
  void findByPersonId_ShouldReturnEmptyWhenNoMatches() {
    when(revocationNavigationRepository.findByPersonId("191212121212")).thenReturn(List.of());

    final var result = service.findByPersonId("191212121212");

    assertTrue(result.isEmpty());
  }
}
