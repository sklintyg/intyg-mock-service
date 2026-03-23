package se.inera.intyg.intygmockservice.application.navigation.staff;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.model.Staff;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;
import se.inera.intyg.intygmockservice.domain.navigation.repository.StaffNavigationRepository;

@Service
@RequiredArgsConstructor
public class StaffNavigationService {

  private final StaffNavigationRepository staffNavigationRepository;
  private final CertificateNavigationRepository certificateNavigationRepository;

  public List<Staff> findAll() {
    return staffNavigationRepository.findAll();
  }

  public Optional<Staff> findById(final String staffId) {
    return staffNavigationRepository.findById(staffId);
  }

  public List<Certificate> findCertificatesByStaffId(final String staffId) {
    return certificateNavigationRepository.findByStaffId(staffId);
  }
}
