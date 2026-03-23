package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.domain.navigation.model.Staff;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;
import se.inera.intyg.intygmockservice.domain.navigation.repository.StaffNavigationRepository;

@Repository
@RequiredArgsConstructor
public class StaffNavigationRepositoryImpl implements StaffNavigationRepository {

  private final CertificateNavigationRepository certificateNavigationRepository;

  @Override
  public List<Staff> findAll() {
    final var map = new LinkedHashMap<String, Staff>();
    certificateNavigationRepository.findAll().stream()
        .filter(c -> c.getIssuedBy() != null)
        .map(c -> c.getIssuedBy())
        .filter(s -> s.getStaffId() != null)
        .forEach(s -> map.putIfAbsent(s.getStaffId(), s));
    return map.values().stream().toList();
  }

  @Override
  public Optional<Staff> findById(final String staffId) {
    return certificateNavigationRepository.findAll().stream()
        .filter(c -> c.getIssuedBy() != null)
        .map(c -> c.getIssuedBy())
        .filter(s -> staffId.equals(s.getStaffId()))
        .findFirst();
  }
}
