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

  @Test
  void findAllPaginated_ShouldReturnFirstPage() {
    final var certs =
        List.of(
            Certificate.builder().certificateId("cert-001").build(),
            Certificate.builder().certificateId("cert-002").build(),
            Certificate.builder().certificateId("cert-003").build());
    when(repository.findAll()).thenReturn(certs);

    final var result = service.findAll(0, 2);

    assertEquals(2, result.content().size());
    assertEquals("cert-001", result.content().get(0).getCertificateId());
    assertEquals("cert-002", result.content().get(1).getCertificateId());
    assertEquals(0, result.page());
    assertEquals(2, result.size());
    assertEquals(3, result.totalElements());
  }

  @Test
  void findAllPaginated_ShouldReturnLastPartialPage() {
    final var certs =
        List.of(
            Certificate.builder().certificateId("cert-001").build(),
            Certificate.builder().certificateId("cert-002").build(),
            Certificate.builder().certificateId("cert-003").build());
    when(repository.findAll()).thenReturn(certs);

    final var result = service.findAll(1, 2);

    assertEquals(1, result.content().size());
    assertEquals("cert-003", result.content().get(0).getCertificateId());
  }

  @Test
  void findAllPaginated_ShouldReturnEmptyWhenPageBeyondRange() {
    final var certs = List.of(Certificate.builder().certificateId("cert-001").build());
    when(repository.findAll()).thenReturn(certs);

    final var result = service.findAll(5, 20);

    assertTrue(result.content().isEmpty());
    assertEquals(1, result.totalElements());
  }
}
