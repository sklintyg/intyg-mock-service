package se.inera.intyg.intygmockservice.application.navigation.certificate;

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
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;

@ExtendWith(MockitoExtension.class)
class CertificateNavigationServiceTest {

  @Mock private CertificateNavigationRepository repository;

  @InjectMocks private CertificateNavigationService service;

  @Test
  void findAll_ShouldDelegateToRepository() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();
    when(repository.findAll()).thenReturn(List.of(certificate));

    final var result = service.findAll();

    assertEquals(List.of(certificate), result);
  }

  @Test
  void findById_ShouldDelegateToRepository() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();
    when(repository.findById("cert-001")).thenReturn(Optional.of(certificate));

    final var result = service.findById("cert-001");

    assertTrue(result.isPresent());
    assertEquals("cert-001", result.get().getCertificateId());
  }

  @Test
  void findById_ShouldReturnEmptyWhenNotFound() {
    when(repository.findById("nonexistent")).thenReturn(Optional.empty());

    assertTrue(service.findById("nonexistent").isEmpty());
  }
}
