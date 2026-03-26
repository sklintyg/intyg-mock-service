package se.inera.intyg.intygmockservice.domain.navigation.repository;

import java.util.List;
import java.util.Optional;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.model.Revocation;

public interface RevocationNavigationRepository {

  Optional<Revocation> findByCertificateId(String certificateId);

  List<Revocation> findByPersonId(PersonId personId);
}
