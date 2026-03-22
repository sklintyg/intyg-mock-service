package se.inera.intyg.intygmockservice.domain.navigation.repository;

import java.util.Optional;
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;

public interface PatientNavigationRepository {

  Optional<Patient> findByPersonId(String normalizedPersonId);
}
