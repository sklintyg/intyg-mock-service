package se.inera.intyg.intygmockservice.domain.navigation.repository;

import java.util.List;
import java.util.Optional;
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;

public interface PatientNavigationRepository {

  List<Patient> findAll();

  Optional<Patient> findByPersonId(PersonId personId);
}
