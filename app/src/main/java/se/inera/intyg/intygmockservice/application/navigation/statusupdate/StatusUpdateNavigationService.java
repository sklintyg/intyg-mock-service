package se.inera.intyg.intygmockservice.application.navigation.statusupdate;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.domain.navigation.model.StatusUpdate;
import se.inera.intyg.intygmockservice.domain.navigation.repository.StatusUpdateNavigationRepository;

@Service
@RequiredArgsConstructor
public class StatusUpdateNavigationService {

  private final StatusUpdateNavigationRepository statusUpdateNavigationRepository;

  public List<StatusUpdate> findAll() {
    return statusUpdateNavigationRepository.findAll();
  }

  public List<StatusUpdate> findByCertificateId(final String certificateId) {
    return statusUpdateNavigationRepository.findByCertificateId(certificateId);
  }

  public List<StatusUpdate> findByPersonId(final String normalizedPersonId) {
    return statusUpdateNavigationRepository.findByPersonId(normalizedPersonId);
  }
}
