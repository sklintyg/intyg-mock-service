package se.inera.intyg.intygmockservice.sendmessagetorecipient.repository;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;

@Repository
public class SendMessageToRecipientRepository
    extends AbstractInMemoryRepository<SendMessageToRecipientType> {

  public SendMessageToRecipientRepository(
      @Value("${app.repository.send-message-to-recipient.max-size:1000}") int maxSize) {
    super(maxSize);
  }

  public Collection<SendMessageToRecipientType> findByRecipientId(String recipientId) {
    return null;
  }
}
