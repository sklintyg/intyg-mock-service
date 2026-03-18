package se.inera.intyg.intygmockservice.statusupdates.passthrough;

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
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponseType;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@ExtendWith(MockitoExtension.class)
class CertificateStatusUpdateForCarePassthroughClientTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";
  private static final String UPSTREAM_URL = "http://upstream/service";

  @Mock private PassthroughClientFactory factory;

  @Test
  void shouldNotCreateProxyWhenDisabled() {
    final var props = propsWithEnabled(false);

    new CertificateStatusUpdateForCarePassthroughClient(props, factory);

    verify(factory, never()).createClient(any(), any());
  }

  @Test
  void shouldNotCreateProxyWhenConfigIsNull() {
    final var props = new PassthroughProperties(null, null, null, null, null, null);

    new CertificateStatusUpdateForCarePassthroughClient(props, factory);

    verify(factory, never()).createClient(any(), any());
  }

  @Test
  void shouldReturnEmptyWhenDisabled() {
    final var client =
        new CertificateStatusUpdateForCarePassthroughClient(propsWithEnabled(false), factory);

    final var result = client.forward(LOGICAL_ADDRESS, new CertificateStatusUpdateForCareType());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyWhenConfigIsNull() {
    final var client =
        new CertificateStatusUpdateForCarePassthroughClient(
            new PassthroughProperties(null, null, null, null, null, null), factory);

    final var result = client.forward(LOGICAL_ADDRESS, new CertificateStatusUpdateForCareType());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnUpstreamResponseWhenEnabled() {
    final var proxy = mock(CertificateStatusUpdateForCareResponderInterface.class);
    when(factory.createClient(
            eq(CertificateStatusUpdateForCareResponderInterface.class), eq(UPSTREAM_URL)))
        .thenReturn(proxy);
    final var expected = okResponse();
    when(proxy.certificateStatusUpdateForCare(any(), any())).thenReturn(expected);

    final var client =
        new CertificateStatusUpdateForCarePassthroughClient(propsWithEnabled(true), factory);
    final var request = new CertificateStatusUpdateForCareType();
    final var result = client.forward(LOGICAL_ADDRESS, request);

    assertTrue(result.isPresent());
    assertEquals(ResultCodeType.OK, result.get().getResult().getResultCode());
    verify(proxy).certificateStatusUpdateForCare(LOGICAL_ADDRESS, request);
  }

  @Test
  void shouldReturnErrorResponseWhenProxyThrows() {
    final var proxy = mock(CertificateStatusUpdateForCareResponderInterface.class);
    when(factory.createClient(
            eq(CertificateStatusUpdateForCareResponderInterface.class), eq(UPSTREAM_URL)))
        .thenReturn(proxy);
    when(proxy.certificateStatusUpdateForCare(any(), any()))
        .thenThrow(new RuntimeException("upstream down"));

    final var client =
        new CertificateStatusUpdateForCarePassthroughClient(propsWithEnabled(true), factory);
    final var result = client.forward(LOGICAL_ADDRESS, new CertificateStatusUpdateForCareType());

    assertTrue(result.isPresent());
    assertEquals(ResultCodeType.ERROR, result.get().getResult().getResultCode());
  }

  private PassthroughProperties propsWithEnabled(boolean enabled) {
    final var config = new ServiceConfig(enabled, UPSTREAM_URL);
    return new PassthroughProperties(null, null, null, null, config, null);
  }

  private CertificateStatusUpdateForCareResponseType okResponse() {
    final var response = new CertificateStatusUpdateForCareResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }
}
