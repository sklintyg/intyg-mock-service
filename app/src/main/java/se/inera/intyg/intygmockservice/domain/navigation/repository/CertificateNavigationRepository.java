package se.inera.intyg.intygmockservice.domain.navigation.repository;

import java.util.List;
import java.util.Optional;
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;

public interface CertificateNavigationRepository {

  List<Certificate> findAll();

  Optional<Certificate> findById(String certificateId);

  List<Certificate> findByPersonId(String normalizedPersonId);

  List<Certificate> findByUnitId(String unitId);

  List<Certificate> findByStaffId(String staffId);
}
