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
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;

@ExtendWith(MockitoExtension.class)
class StaffNavigationRepositoryImplTest {

  @Mock private CertificateNavigationRepository certificateNavigationRepository;

  @InjectMocks private StaffNavigationRepositoryImpl repository;

  @Test
  void findAll_ShouldReturnUniqueStaffFromCertificates() {
    final var staff = Staff.builder().staffId("staff-001").fullName("Anna Läkare").build();
    final var certificate = Certificate.builder().certificateId("cert-001").issuedBy(staff).build();

    when(certificateNavigationRepository.findAll()).thenReturn(List.of(certificate));

    final var result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals("staff-001", result.get(0).getStaffId());
  }

  @Test
  void findAll_ShouldDeduplicateStaffWithSameId() {
    final var staff = Staff.builder().staffId("staff-001").build();
    final var cert1 = Certificate.builder().certificateId("cert-001").issuedBy(staff).build();
    final var cert2 = Certificate.builder().certificateId("cert-002").issuedBy(staff).build();

    when(certificateNavigationRepository.findAll()).thenReturn(List.of(cert1, cert2));

    final var result = repository.findAll();

    assertEquals(1, result.size());
  }

  @Test
  void findAll_ShouldSkipCertificatesWithNoIssuedBy() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();

    when(certificateNavigationRepository.findAll()).thenReturn(List.of(certificate));

    final var result = repository.findAll();

    assertTrue(result.isEmpty());
  }

  @Test
  void findAll_ShouldSkipStaffWithNullId() {
    final var staff = Staff.builder().fullName("Anna Läkare").build();
    final var certificate = Certificate.builder().certificateId("cert-001").issuedBy(staff).build();

    when(certificateNavigationRepository.findAll()).thenReturn(List.of(certificate));

    final var result = repository.findAll();

    assertTrue(result.isEmpty());
  }

  @Test
  void findById_ShouldReturnMatchingStaff() {
    final var staff = Staff.builder().staffId("staff-001").fullName("Anna Läkare").build();
    final var certificate = Certificate.builder().certificateId("cert-001").issuedBy(staff).build();

    when(certificateNavigationRepository.findAll()).thenReturn(List.of(certificate));

    final var result = repository.findById("staff-001");

    assertTrue(result.isPresent());
    assertEquals("staff-001", result.get().getStaffId());
    assertEquals("Anna Läkare", result.get().getFullName());
  }

  @Test
  void findById_ShouldReturnEmptyWhenNotFound() {
    when(certificateNavigationRepository.findAll()).thenReturn(List.of());

    final var result = repository.findById("unknown");

    assertTrue(result.isEmpty());
  }
}
