package se.inera.intyg.intygmockservice.application.sendmessagetorecipient.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.service.SendMessageToRecipientService;
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
    return service
        .store(logicalAddress, sendMessageToRecipient)
        .orElseGet(
            () -> {
              final var response = new SendMessageToRecipientResponseType();
              final var result = new ResultType();
              result.setResultCode(ResultCodeType.OK);
              response.setResult(result);
              return response;
            });
  }
}
