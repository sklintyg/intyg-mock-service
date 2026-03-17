package se.inera.intyg.intygmockservice.sendmessagetorecipient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientResponseType;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Service
@RequiredArgsConstructor
public class SendMessageToRecipientResponderImpl
    implements SendMessageToRecipientResponderInterface {

  private final SendMessageToRecipientService service;

  @Override
  public SendMessageToRecipientResponseType sendMessageToRecipient(
      String logicalAddress, SendMessageToRecipientType sendMessageToRecipient) {
    service.store(logicalAddress, sendMessageToRecipient);

    final var response = new SendMessageToRecipientResponseType();
    final var result = new ResultType();
    response.setResult(result);
    result.setResultCode(ResultCodeType.OK);

    return response;
  }
}
