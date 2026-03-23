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
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;

@ExtendWith(MockitoExtension.class)
class PatientNavigationRepositoryImplTest {

  @Mock private CertificateNavigationRepository certificateNavigationRepository;

  @InjectMocks private PatientNavigationRepositoryImpl repository;

  @Test
  void findByPersonId_ShouldReturnPatientFromFirstMatchingCertificate() {
    final var patient =
        Patient.builder().personId("191212121212").firstName("Test").lastName("Testsson").build();
    final var certificate =
        Certificate.builder().certificateId("cert-001").patient(patient).build();

    when(certificateNavigationRepository.findByPersonId("191212121212"))
        .thenReturn(List.of(certificate));

    final var result = repository.findByPersonId("191212121212");

    assertTrue(result.isPresent());
    assertEquals("191212121212", result.get().getPersonId());
    assertEquals("Test", result.get().getFirstName());
  }

  @Test
  void findByPersonId_ShouldReturnEmptyWhenNoCertificatesFound() {
    when(certificateNavigationRepository.findByPersonId("unknown")).thenReturn(List.of());

    final var result = repository.findByPersonId("unknown");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByPersonId_ShouldReturnEmptyWhenCertificateHasNullPatient() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();

    when(certificateNavigationRepository.findByPersonId("191212121212"))
        .thenReturn(List.of(certificate));

    final var result = repository.findByPersonId("191212121212");

    assertTrue(result.isEmpty());
  }
}
