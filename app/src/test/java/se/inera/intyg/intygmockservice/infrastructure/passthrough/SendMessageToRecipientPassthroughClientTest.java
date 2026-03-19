package se.inera.intyg.intygmockservice.infrastructure.passthrough;

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
import se.inera.intyg.intygmockservice.infrastructure.config.passthrough.PassthroughClientFactory;
import se.inera.intyg.intygmockservice.infrastructure.config.properties.PassthroughProperties;
import se.inera.intyg.intygmockservice.infrastructure.config.properties.PassthroughProperties.ServiceConfig;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientResponseType;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@ExtendWith(MockitoExtension.class)
class SendMessageToRecipientPassthroughClientTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";
  private static final String UPSTREAM_URL = "http://upstream/service";

  @Mock private PassthroughClientFactory factory;

  @Test
  void shouldNotCreateProxyWhenDisabled() {
    final var props = propsWithEnabled(false);

    new SendMessageToRecipientPassthroughClient(props, factory);

    verify(factory, never()).createClient(any(), any());
  }

  @Test
  void shouldNotCreateProxyWhenConfigIsNull() {
    final var props = new PassthroughProperties(null, null, null, null, null, null);

    new SendMessageToRecipientPassthroughClient(props, factory);

    verify(factory, never()).createClient(any(), any());
  }

  @Test
  void shouldReturnEmptyWhenDisabled() {
    final var client =
        new SendMessageToRecipientPassthroughClient(propsWithEnabled(false), factory);

    final var result = client.forward(LOGICAL_ADDRESS, new SendMessageToRecipientType());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyWhenConfigIsNull() {
    final var client =
        new SendMessageToRecipientPassthroughClient(
            new PassthroughProperties(null, null, null, null, null, null), factory);

    final var result = client.forward(LOGICAL_ADDRESS, new SendMessageToRecipientType());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnUpstreamResponseWhenEnabled() {
    final var proxy = mock(SendMessageToRecipientResponderInterface.class);
    when(factory.createClient(eq(SendMessageToRecipientResponderInterface.class), eq(UPSTREAM_URL)))
        .thenReturn(proxy);
    final var expected = okResponse();
    when(proxy.sendMessageToRecipient(any(), any())).thenReturn(expected);

    final var client = new SendMessageToRecipientPassthroughClient(propsWithEnabled(true), factory);
    final var request = new SendMessageToRecipientType();
    final var result = client.forward(LOGICAL_ADDRESS, request);

    assertTrue(result.isPresent());
    assertEquals(ResultCodeType.OK, result.get().getResult().getResultCode());
    verify(proxy).sendMessageToRecipient(LOGICAL_ADDRESS, request);
  }

  @Test
  void shouldReturnErrorResponseWhenProxyThrows() {
    final var proxy = mock(SendMessageToRecipientResponderInterface.class);
    when(factory.createClient(eq(SendMessageToRecipientResponderInterface.class), eq(UPSTREAM_URL)))
        .thenReturn(proxy);
    when(proxy.sendMessageToRecipient(any(), any()))
        .thenThrow(new RuntimeException("upstream down"));

    final var client = new SendMessageToRecipientPassthroughClient(propsWithEnabled(true), factory);
    final var result = client.forward(LOGICAL_ADDRESS, new SendMessageToRecipientType());

    assertTrue(result.isPresent());
    assertEquals(ResultCodeType.ERROR, result.get().getResult().getResultCode());
  }

  private PassthroughProperties propsWithEnabled(boolean enabled) {
    final var config = new ServiceConfig(enabled, UPSTREAM_URL);
    return new PassthroughProperties(null, null, null, config, null, null);
  }

  private SendMessageToRecipientResponseType okResponse() {
    final var response = new SendMessageToRecipientResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }
}
