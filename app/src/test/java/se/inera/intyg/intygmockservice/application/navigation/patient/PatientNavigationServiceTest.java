package se.inera.intyg.intygmockservice.application.navigation.patient;

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
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;
import se.inera.intyg.intygmockservice.domain.navigation.repository.PatientNavigationRepository;

@ExtendWith(MockitoExtension.class)
class PatientNavigationServiceTest {

  @Mock private PatientNavigationRepository patientNavigationRepository;
  @Mock private CertificateNavigationRepository certificateNavigationRepository;

  @InjectMocks private PatientNavigationService service;

  @Test
  void findByPersonId_ShouldDelegateToRepository() {
    final var patient = Patient.builder().personId(PersonId.of("191212121212")).build();
    when(patientNavigationRepository.findByPersonId(PersonId.of("191212121212")))
        .thenReturn(Optional.of(patient));

    final var result = service.findByPersonId("191212121212");

    assertTrue(result.isPresent());
    assertEquals(PersonId.of("191212121212"), result.get().getPersonId());
    verify(patientNavigationRepository).findByPersonId(PersonId.of("191212121212"));
  }

  @Test
  void findCertificatesByPersonId_ShouldDelegateToRepository() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();
    when(certificateNavigationRepository.findByPersonId("191212121212"))
        .thenReturn(List.of(certificate));

    final var result = service.findCertificatesByPersonId("191212121212");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
    verify(certificateNavigationRepository).findByPersonId("191212121212");
  }
}
