package se.inera.intyg.intygmockservice.application.navigation.patient;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;
import se.inera.intyg.intygmockservice.domain.navigation.repository.PatientNavigationRepository;

@Service
@RequiredArgsConstructor
public class PatientNavigationService {

  private final PatientNavigationRepository patientNavigationRepository;
  private final CertificateNavigationRepository certificateNavigationRepository;

  public Optional<Patient> findByPersonId(final String normalizedPersonId) {
    return patientNavigationRepository.findByPersonId(normalizedPersonId);
  }

  public List<Certificate> findCertificatesByPersonId(final String normalizedPersonId) {
    return certificateNavigationRepository.findByPersonId(normalizedPersonId);
  }
}
