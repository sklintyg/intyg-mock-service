package se.inera.intyg.intygmockservice.domain.navigation.repository;

import java.util.List;
import java.util.Optional;
import se.inera.intyg.intygmockservice.domain.navigation.model.Staff;

public interface StaffNavigationRepository {

  List<Staff> findAll();

  Optional<Staff> findById(String staffId);
}
