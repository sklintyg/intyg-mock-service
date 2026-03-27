package se.inera.intyg.intygmockservice.application.navigation.message;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.domain.navigation.model.Message;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.repository.MessageNavigationRepository;

@Service
@RequiredArgsConstructor
public class MessageNavigationService {

  private final MessageNavigationRepository messageNavigationRepository;

  public List<Message> findAll() {
    return messageNavigationRepository.findAll();
  }

  public Optional<Message> findById(final String messageId) {
    return messageNavigationRepository.findById(messageId);
  }

  public List<Message> findByCertificateId(final String certificateId) {
    return messageNavigationRepository.findByCertificateId(certificateId);
  }

  public List<Message> findByPersonId(final String normalizedPersonId) {
    return messageNavigationRepository.findByPersonId(PersonId.of(normalizedPersonId));
  }
}
