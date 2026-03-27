package se.inera.intyg.intygmockservice.application.navigation.revocation;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.model.Revocation;
import se.inera.intyg.intygmockservice.domain.navigation.repository.RevocationNavigationRepository;

@Service
@RequiredArgsConstructor
public class RevocationNavigationService {

  private final RevocationNavigationRepository revocationNavigationRepository;

  public Optional<Revocation> findByCertificateId(final String certificateId) {
    return revocationNavigationRepository.findByCertificateId(certificateId);
  }

  public List<Revocation> findByPersonId(final String normalizedPersonId) {
    return revocationNavigationRepository.findByPersonId(PersonId.of(normalizedPersonId));
  }
}
