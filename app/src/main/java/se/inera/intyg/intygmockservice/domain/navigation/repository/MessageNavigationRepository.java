package se.inera.intyg.intygmockservice.domain.navigation.repository;

import java.util.List;
import java.util.Optional;
import se.inera.intyg.intygmockservice.domain.navigation.model.Message;

public interface MessageNavigationRepository {

  List<Message> findAll();

  Optional<Message> findById(String messageId);

  List<Message> findByCertificateId(String certificateId);

  List<Message> findByPersonId(String normalizedPersonId);
}
