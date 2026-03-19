package se.inera.intyg.intygmockservice.sendmessagetorecipient.repository;

import java.util.List;
import java.util.Optional;
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

  public Optional<SendMessageToRecipientType> findByMessageId(String messageId) {
    return findAll().stream().filter(t -> messageId.equals(t.getMeddelandeId())).findFirst();
  }

  public void deleteByMessageId(String messageId) {
    removeIf(t -> messageId.equals(t.getMeddelandeId()));
  }

  public List<SendMessageToRecipientType> findByCertificateId(String certificateId) {
    return findAll().stream()
        .filter(t -> certificateId.equals(t.getIntygsId().getExtension()))
        .toList();
  }

  public List<SendMessageToRecipientType> findByPersonId(String normalizedPersonId) {
    return findAll().stream()
        .filter(t -> normalizedPersonId.equals(normalize(t.getPatientPersonId().getExtension())))
        .toList();
  }

  public List<SendMessageToRecipientType> findByLogicalAddress(String logicalAddress) {
    return findByKey(logicalAddress);
  }

  private static String normalize(String personId) {
    return personId == null ? null : personId.replace("-", "");
  }
}
