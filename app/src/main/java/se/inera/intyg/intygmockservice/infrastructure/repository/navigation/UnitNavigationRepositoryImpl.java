package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.domain.navigation.model.Unit;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;
import se.inera.intyg.intygmockservice.domain.navigation.repository.UnitNavigationRepository;

@Repository
@RequiredArgsConstructor
public class UnitNavigationRepositoryImpl implements UnitNavigationRepository {

  private final CertificateNavigationRepository certificateNavigationRepository;

  @Override
  public List<Unit> findAll() {
    final var map = new LinkedHashMap<String, Unit>();
    certificateNavigationRepository.findAll().stream()
        .filter(c -> c.getIssuedBy() != null && c.getIssuedBy().getUnit() != null)
        .map(c -> c.getIssuedBy().getUnit())
        .filter(u -> u.getUnitId() != null)
        .forEach(u -> map.putIfAbsent(u.getUnitId(), u));
    return map.values().stream().toList();
  }

  @Override
  public Optional<Unit> findById(final String unitId) {
    return certificateNavigationRepository.findAll().stream()
        .filter(c -> c.getIssuedBy() != null && c.getIssuedBy().getUnit() != null)
        .map(c -> c.getIssuedBy().getUnit())
        .filter(u -> unitId.equals(u.getUnitId()))
        .findFirst();
  }
}
