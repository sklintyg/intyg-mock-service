package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.model.Staff;
import se.inera.intyg.intygmockservice.domain.navigation.model.Unit;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;

@ExtendWith(MockitoExtension.class)
class UnitNavigationRepositoryImplTest {

  @Mock private CertificateNavigationRepository certificateNavigationRepository;

  @InjectMocks private UnitNavigationRepositoryImpl repository;

  @Test
  void findAll_ShouldReturnUniqueUnitsFromCertificates() {
    final var unit = Unit.builder().unitId("unit-001").unitName("Test Unit").build();
    final var staff = Staff.builder().staffId("staff-001").unit(unit).build();
    final var certificate = Certificate.builder().certificateId("cert-001").issuedBy(staff).build();

    when(certificateNavigationRepository.findAll()).thenReturn(List.of(certificate));

    final var result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals("unit-001", result.get(0).getUnitId());
  }

  @Test
  void findAll_ShouldDeduplicateUnitsWithSameId() {
    final var unit = Unit.builder().unitId("unit-001").unitName("Test Unit").build();
    final var staff = Staff.builder().staffId("staff-001").unit(unit).build();
    final var cert1 = Certificate.builder().certificateId("cert-001").issuedBy(staff).build();
    final var cert2 = Certificate.builder().certificateId("cert-002").issuedBy(staff).build();

    when(certificateNavigationRepository.findAll()).thenReturn(List.of(cert1, cert2));

    final var result = repository.findAll();

    assertEquals(1, result.size());
  }

  @Test
  void findAll_ShouldSkipCertificatesWithNoUnit() {
    final var staff = Staff.builder().staffId("staff-001").build();
    final var certificate = Certificate.builder().certificateId("cert-001").issuedBy(staff).build();

    when(certificateNavigationRepository.findAll()).thenReturn(List.of(certificate));

    final var result = repository.findAll();

    assertTrue(result.isEmpty());
  }

  @Test
  void findAll_ShouldSkipCertificatesWithNoIssuedBy() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();

    when(certificateNavigationRepository.findAll()).thenReturn(List.of(certificate));

    final var result = repository.findAll();

    assertTrue(result.isEmpty());
  }

  @Test
  void findById_ShouldReturnMatchingUnit() {
    final var unit = Unit.builder().unitId("unit-001").unitName("Test Unit").build();
    final var staff = Staff.builder().staffId("staff-001").unit(unit).build();
    final var certificate = Certificate.builder().certificateId("cert-001").issuedBy(staff).build();

    when(certificateNavigationRepository.findAll()).thenReturn(List.of(certificate));

    final var result = repository.findById("unit-001");

    assertTrue(result.isPresent());
    assertEquals("unit-001", result.get().getUnitId());
    assertEquals("Test Unit", result.get().getUnitName());
  }

  @Test
  void findById_ShouldReturnEmptyWhenNotFound() {
    when(certificateNavigationRepository.findAll()).thenReturn(List.of());

    final var result = repository.findById("unknown");

    assertTrue(result.isEmpty());
  }
}
