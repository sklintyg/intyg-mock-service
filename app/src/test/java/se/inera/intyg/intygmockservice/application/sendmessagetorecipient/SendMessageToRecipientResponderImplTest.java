package se.inera.intyg.intygmockservice.application.sendmessagetorecipient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientResponseType;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@ExtendWith(MockitoExtension.class)
class SendMessageToRecipientResponderImplTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";

  @Mock private SendMessageToRecipientService service;

  @InjectMocks private SendMessageToRecipientResponderImpl responder;

  @Test
  void shouldReturnLocalOkWhenServiceReturnsEmpty() {
    final var type = new SendMessageToRecipientType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.empty());

    final var response = responder.sendMessageToRecipient(LOGICAL_ADDRESS, type);

    assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
  }

  @Test
  void shouldReturnUpstreamResponseWhenServiceReturnsPresent() {
    final var upstreamResponse = okResponse();
    final var type = new SendMessageToRecipientType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.of(upstreamResponse));

    final var response = responder.sendMessageToRecipient(LOGICAL_ADDRESS, type);

    assertSame(upstreamResponse, response);
  }

  @Test
  void shouldDelegateToService() {
    final var type = new SendMessageToRecipientType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.empty());

    responder.sendMessageToRecipient(LOGICAL_ADDRESS, type);

    verify(service).store(LOGICAL_ADDRESS, type);
  }

  private SendMessageToRecipientResponseType okResponse() {
    final var response = new SendMessageToRecipientResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }
}
