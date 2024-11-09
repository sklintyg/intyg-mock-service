package se.inera.intyg.intygmockservice.sendmessagetorecipient.repository;

import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;

@Repository
public class SendMessageToRecipientRepository extends AbstractInMemoryRepository<SendMessageToRecipientType> {

}