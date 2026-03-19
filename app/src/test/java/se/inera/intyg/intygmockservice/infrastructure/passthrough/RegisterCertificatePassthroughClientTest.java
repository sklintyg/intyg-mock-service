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
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@ExtendWith(MockitoExtension.class)
class RegisterCertificatePassthroughClientTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";
  private static final String UPSTREAM_URL = "http://upstream/service";

  @Mock private PassthroughClientFactory factory;

  @Test
  void shouldNotCreateProxyWhenDisabled() {
    final var props = propsWithEnabled(false);

    new RegisterCertificatePassthroughClient(props, factory);

    verify(factory, never()).createClient(any(), any());
  }

  @Test
  void shouldNotCreateProxyWhenConfigIsNull() {
    final var props = new PassthroughProperties(null, null, null, null, null, null);

    new RegisterCertificatePassthroughClient(props, factory);

    verify(factory, never()).createClient(any(), any());
  }

  @Test
  void shouldReturnEmptyWhenDisabled() {
    final var client = new RegisterCertificatePassthroughClient(propsWithEnabled(false), factory);

    final var result = client.forward(LOGICAL_ADDRESS, new RegisterCertificateType());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyWhenConfigIsNull() {
    final var client =
        new RegisterCertificatePassthroughClient(
            new PassthroughProperties(null, null, null, null, null, null), factory);

    final var result = client.forward(LOGICAL_ADDRESS, new RegisterCertificateType());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnUpstreamResponseWhenEnabled() {
    final var proxy = mock(RegisterCertificateResponderInterface.class);
    when(factory.createClient(eq(RegisterCertificateResponderInterface.class), eq(UPSTREAM_URL)))
        .thenReturn(proxy);
    final var expected = okResponse();
    when(proxy.registerCertificate(any(), any())).thenReturn(expected);

    final var client = new RegisterCertificatePassthroughClient(propsWithEnabled(true), factory);
    final var request = new RegisterCertificateType();
    final var result = client.forward(LOGICAL_ADDRESS, request);

    assertTrue(result.isPresent());
    assertEquals(ResultCodeType.OK, result.get().getResult().getResultCode());
    verify(proxy).registerCertificate(LOGICAL_ADDRESS, request);
  }

  @Test
  void shouldReturnErrorResponseWhenProxyThrows() {
    final var proxy = mock(RegisterCertificateResponderInterface.class);
    when(factory.createClient(eq(RegisterCertificateResponderInterface.class), eq(UPSTREAM_URL)))
        .thenReturn(proxy);
    when(proxy.registerCertificate(any(), any())).thenThrow(new RuntimeException("upstream down"));

    final var client = new RegisterCertificatePassthroughClient(propsWithEnabled(true), factory);
    final var result = client.forward(LOGICAL_ADDRESS, new RegisterCertificateType());

    assertTrue(result.isPresent());
    assertEquals(ResultCodeType.ERROR, result.get().getResult().getResultCode());
  }

  private PassthroughProperties propsWithEnabled(boolean enabled) {
    final var config = new ServiceConfig(enabled, UPSTREAM_URL);
    return new PassthroughProperties(null, config, null, null, null, null);
  }

  private RegisterCertificateResponseType okResponse() {
    final var response = new RegisterCertificateResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }
}
