package se.inera.intyg.intygmockservice.application.storelog;

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
import se.inera.intyg.intygmockservice.application.storelog.service.StoreLogService;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogResponseType;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;
import se.riv.informationsecurity.auditing.log.v2.ResultCodeType;
import se.riv.informationsecurity.auditing.log.v2.ResultType;

@ExtendWith(MockitoExtension.class)
class StoreLogResponderImplTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";

  @Mock private StoreLogService service;

  @InjectMocks private StoreLogResponderImpl responder;

  @Test
  void shouldReturnLocalOkWhenServiceReturnsEmpty() {
    final var type = new StoreLogType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.empty());

    final var response = responder.storeLog(LOGICAL_ADDRESS, type);

    assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
  }

  @Test
  void shouldReturnUpstreamResponseWhenServiceReturnsPresent() {
    final var upstreamResponse = okResponse();
    final var type = new StoreLogType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.of(upstreamResponse));

    final var response = responder.storeLog(LOGICAL_ADDRESS, type);

    assertSame(upstreamResponse, response);
  }

  @Test
  void shouldDelegateToService() {
    final var type = new StoreLogType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.empty());

    responder.storeLog(LOGICAL_ADDRESS, type);

    verify(service).store(LOGICAL_ADDRESS, type);
  }

  private StoreLogResponseType okResponse() {
    final var response = new StoreLogResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }
}
