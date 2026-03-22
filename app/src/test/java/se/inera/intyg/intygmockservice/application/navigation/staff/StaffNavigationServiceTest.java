package se.inera.intyg.intygmockservice.application.navigation.staff;

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
import se.inera.intyg.intygmockservice.domain.navigation.model.Staff;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;
import se.inera.intyg.intygmockservice.domain.navigation.repository.StaffNavigationRepository;

@ExtendWith(MockitoExtension.class)
class StaffNavigationServiceTest {

  @Mock private StaffNavigationRepository staffNavigationRepository;
  @Mock private CertificateNavigationRepository certificateNavigationRepository;

  @InjectMocks private StaffNavigationService service;

  @Test
  void findAll_ShouldDelegateToRepository() {
    final var staff = Staff.builder().staffId("staff-001").build();
    when(staffNavigationRepository.findAll()).thenReturn(List.of(staff));

    final var result = service.findAll();

    assertEquals(1, result.size());
    assertEquals("staff-001", result.get(0).getStaffId());
    verify(staffNavigationRepository).findAll();
  }

  @Test
  void findById_ShouldDelegateToRepository() {
    final var staff = Staff.builder().staffId("staff-001").build();
    when(staffNavigationRepository.findById("staff-001")).thenReturn(Optional.of(staff));

    final var result = service.findById("staff-001");

    assertTrue(result.isPresent());
    assertEquals("staff-001", result.get().getStaffId());
    verify(staffNavigationRepository).findById("staff-001");
  }

  @Test
  void findCertificatesByStaffId_ShouldDelegateToRepository() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();
    when(certificateNavigationRepository.findByStaffId("staff-001"))
        .thenReturn(List.of(certificate));

    final var result = service.findCertificatesByStaffId("staff-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
    verify(certificateNavigationRepository).findByStaffId("staff-001");
  }
}
