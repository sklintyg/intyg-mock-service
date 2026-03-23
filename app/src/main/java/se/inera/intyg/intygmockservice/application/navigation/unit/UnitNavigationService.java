package se.inera.intyg.intygmockservice.application.navigation.unit;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.model.Unit;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;
import se.inera.intyg.intygmockservice.domain.navigation.repository.UnitNavigationRepository;

@Service
@RequiredArgsConstructor
public class UnitNavigationService {

  private final UnitNavigationRepository unitNavigationRepository;
  private final CertificateNavigationRepository certificateNavigationRepository;

  public List<Unit> findAll() {
    return unitNavigationRepository.findAll();
  }

  public Optional<Unit> findById(final String unitId) {
    return unitNavigationRepository.findById(unitId);
  }

  public List<Certificate> findCertificatesByUnitId(final String unitId) {
    return certificateNavigationRepository.findByUnitId(unitId);
  }
}
