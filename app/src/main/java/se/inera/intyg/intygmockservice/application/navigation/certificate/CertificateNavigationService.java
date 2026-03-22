package se.inera.intyg.intygmockservice.application.navigation.certificate;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;

@Service
@RequiredArgsConstructor
public class CertificateNavigationService {

  private final CertificateNavigationRepository repository;

  public List<Certificate> findAll() {
    return repository.findAll();
  }

  public Optional<Certificate> findById(final String certificateId) {
    return repository.findById(certificateId);
  }
}
