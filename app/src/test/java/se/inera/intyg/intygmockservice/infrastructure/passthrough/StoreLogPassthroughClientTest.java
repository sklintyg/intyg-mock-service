package se.inera.intyg.intygmockservice.storelog.passthrough;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.config.passthrough.PassthroughClientFactory;
import se.inera.intyg.intygmockservice.config.properties.PassthroughProperties;
import se.inera.intyg.intygmockservice.config.properties.PassthroughProperties.ServiceConfig;
import se.riv.informationsecurity.auditing.log.StoreLog.v2.rivtabp21.StoreLogResponderInterface;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogResponseType;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;
import se.riv.informationsecurity.auditing.log.v2.ResultCodeType;
import se.riv.informationsecurity.auditing.log.v2.ResultType;

@ExtendWith(MockitoExtension.class)
class StoreLogPassthroughClientTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";
  private static final String UPSTREAM_URL = "http://upstream/service";

  @Mock private PassthroughClientFactory factory;

  @Test
  void shouldNotCreateProxyWhenDisabled() {
    final var props = propsWithEnabled(false);

    new StoreLogPassthroughClient(props, factory);

    verify(factory, never()).createClient(any(), any());
  }

  @Test
  void shouldNotCreateProxyWhenConfigIsNull() {
    final var props = new PassthroughProperties(null, null, null, null, null, null);

    new StoreLogPassthroughClient(props, factory);

    verify(factory, never()).createClient(any(), any());
  }

  @Test
  void shouldReturnEmptyWhenDisabled() {
    final var client = new StoreLogPassthroughClient(propsWithEnabled(false), factory);

    final var result = client.forward(LOGICAL_ADDRESS, new StoreLogType());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyWhenConfigIsNull() {
    final var client =
        new StoreLogPassthroughClient(
            new PassthroughProperties(null, null, null, null, null, null), factory);

    final var result = client.forward(LOGICAL_ADDRESS, new StoreLogType());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnUpstreamResponseWhenEnabled() {
    final var proxy = mock(StoreLogResponderInterface.class);
    when(factory.createClient(eq(StoreLogResponderInterface.class), eq(UPSTREAM_URL)))
        .thenReturn(proxy);
    final var expected = okResponse();
    when(proxy.storeLog(any(), any())).thenReturn(expected);

    final var client = new StoreLogPassthroughClient(propsWithEnabled(true), factory);
    final var request = new StoreLogType();
    final var result = client.forward(LOGICAL_ADDRESS, request);

    assertTrue(result.isPresent());
    assertEquals(ResultCodeType.OK, result.get().getResult().getResultCode());
    verify(proxy).storeLog(LOGICAL_ADDRESS, request);
  }

  @Test
  void shouldReturnErrorResponseWhenProxyThrows() {
    final var proxy = mock(StoreLogResponderInterface.class);
    when(factory.createClient(eq(StoreLogResponderInterface.class), eq(UPSTREAM_URL)))
        .thenReturn(proxy);
    when(proxy.storeLog(any(), any())).thenThrow(new RuntimeException("upstream down"));

    final var client = new StoreLogPassthroughClient(propsWithEnabled(true), factory);
    final var result = client.forward(LOGICAL_ADDRESS, new StoreLogType());

    assertTrue(result.isPresent());
    assertEquals(ResultCodeType.ERROR, result.get().getResult().getResultCode());
  }

  private PassthroughProperties propsWithEnabled(boolean enabled) {
    final var config = new ServiceConfig(enabled, UPSTREAM_URL);
    return new PassthroughProperties(null, null, null, null, null, config);
  }

  private StoreLogResponseType okResponse() {
    final var response = new StoreLogResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }
}
