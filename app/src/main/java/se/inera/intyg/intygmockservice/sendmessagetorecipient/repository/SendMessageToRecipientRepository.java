package se.inera.intyg.intygmockservice.sendmessagetorecipient.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.inera.intyg.intygmockservice.config.properties.AppProperties;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;

@Repository
public class SendMessageToRecipientRepository
    extends AbstractInMemoryRepository<SendMessageToRecipientType> {

  public SendMessageToRecipientRepository(AppProperties appProperties) {
    super(appProperties.repository().sendMessageToRecipient().maxSize());
  }

  public List<SendMessageToRecipientType> findByRecipientId(String recipientId) {
    return findAll().stream()
        .filter(t -> recipientId.equals(t.getLogiskAdressMottagare()))
        .toList();
  }
}
