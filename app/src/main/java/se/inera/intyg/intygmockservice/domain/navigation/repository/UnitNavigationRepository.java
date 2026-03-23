package se.inera.intyg.intygmockservice.domain.navigation.repository;

import java.util.List;
import java.util.Optional;
import se.inera.intyg.intygmockservice.domain.navigation.model.Unit;

public interface UnitNavigationRepository {

  List<Unit> findAll();

  Optional<Unit> findById(String unitId);
}
