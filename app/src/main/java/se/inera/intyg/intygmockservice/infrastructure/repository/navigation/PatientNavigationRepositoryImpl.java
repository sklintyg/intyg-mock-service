package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;
import se.inera.intyg.intygmockservice.domain.navigation.repository.PatientNavigationRepository;

@Repository
@RequiredArgsConstructor
public class PatientNavigationRepositoryImpl implements PatientNavigationRepository {

  private final CertificateNavigationRepository certificateNavigationRepository;

  @Override
  public List<Patient> findAll() {
    return certificateNavigationRepository.findAll().stream()
        .map(c -> c.getPatient())
        .filter(p -> p != null && p.getPersonId() != null)
        .collect(Collectors.toMap(Patient::getPersonId, p -> p, (a, b) -> a))
        .values()
        .stream()
        .toList();
  }

  @Override
  public Optional<Patient> findByPersonId(final String normalizedPersonId) {
    return certificateNavigationRepository.findByPersonId(normalizedPersonId).stream()
        .map(c -> c.getPatient())
        .filter(p -> p != null)
        .findFirst();
  }
}
