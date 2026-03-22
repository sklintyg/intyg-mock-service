package se.inera.intyg.intygmockservice.application.navigation.unit;

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
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.model.Unit;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;
import se.inera.intyg.intygmockservice.domain.navigation.repository.UnitNavigationRepository;

@ExtendWith(MockitoExtension.class)
class UnitNavigationServiceTest {

  @Mock private UnitNavigationRepository unitNavigationRepository;
  @Mock private CertificateNavigationRepository certificateNavigationRepository;

  @InjectMocks private UnitNavigationService service;

  @Test
  void findAll_ShouldDelegateToRepository() {
    final var unit = Unit.builder().unitId("unit-001").build();
    when(unitNavigationRepository.findAll()).thenReturn(List.of(unit));

    final var result = service.findAll();

    assertEquals(1, result.size());
    assertEquals("unit-001", result.get(0).getUnitId());
    verify(unitNavigationRepository).findAll();
  }

  @Test
  void findById_ShouldDelegateToRepository() {
    final var unit = Unit.builder().unitId("unit-001").build();
    when(unitNavigationRepository.findById("unit-001")).thenReturn(Optional.of(unit));

    final var result = service.findById("unit-001");

    assertTrue(result.isPresent());
    assertEquals("unit-001", result.get().getUnitId());
    verify(unitNavigationRepository).findById("unit-001");
  }

  @Test
  void findCertificatesByUnitId_ShouldDelegateToRepository() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();
    when(certificateNavigationRepository.findByUnitId("unit-001")).thenReturn(List.of(certificate));

    final var result = service.findCertificatesByUnitId("unit-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
    verify(certificateNavigationRepository).findByUnitId("unit-001");
  }
}
