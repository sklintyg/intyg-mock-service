package se.inera.intyg.intygmockservice.domain.navigation.repository;

import java.util.List;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.model.StatusUpdate;

public interface StatusUpdateNavigationRepository {

  List<StatusUpdate> findAll();

  List<StatusUpdate> findByCertificateId(String certificateId);

  List<StatusUpdate> findByPersonId(PersonId personId);
}
