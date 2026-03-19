package se.inera.intyg.intygmockservice.infrastructure.config.passthrough;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.infrastructure.config.properties.PassthroughProperties;
import se.inera.intyg.intygmockservice.infrastructure.config.properties.PassthroughProperties.MtlsConfig;

@ExtendWith(MockitoExtension.class)
class PassthroughConduitConfigurerTest {

  private static final String CLINICAL_PROCESS_NAME =
      "{urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificate:3:rivtabp21}"
          + "RegisterCertificateResponderService.http-conduit";

  private static final String INSURANCE_PROCESS_NAME =
      "{urn:riv:insuranceprocess:healthreporting:RegisterMedicalCertificate:3:rivtabp21}"
          + "Service.http-conduit";

  private static final String NON_MATCHING_NAME = "SomeInternalCxfConduit.http-conduit";

  @Mock private PassthroughProperties props;
  @Mock private HTTPConduit conduit;

  private PassthroughConduitConfigurer configurer;

  @BeforeEach
  void setUp() {
    configurer = new PassthroughConduitConfigurer(props);
  }

  @Test
  void shouldSkipConduitWhenNameIsNull() {
    final var httpConduitConfigurer = configurer.passthroughHttpConduitConfigurer();

    httpConduitConfigurer.configure(null, "http://any-address", conduit);

    verify(conduit, never()).setClient(org.mockito.ArgumentMatchers.any());
    verify(conduit, never()).setTlsClientParameters(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void shouldSkipConduitWhenNameDoesNotMatchPattern() {
    final var httpConduitConfigurer = configurer.passthroughHttpConduitConfigurer();

    httpConduitConfigurer.configure(NON_MATCHING_NAME, "http://any-address", conduit);

    verify(conduit, never()).setClient(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void shouldConfigureConduitForClinicalProcessNamespace() {
    final var httpConduitConfigurer = configurer.passthroughHttpConduitConfigurer();

    assertThrows(
        NullPointerException.class,
        () ->
            httpConduitConfigurer.configure(CLINICAL_PROCESS_NAME, "http://any-address", conduit));

    verify(conduit).setClient(org.mockito.ArgumentMatchers.any(HTTPClientPolicy.class));
  }

  @Test
  void shouldConfigureConduitForInsuranceProcessNamespace() {
    final var httpConduitConfigurer = configurer.passthroughHttpConduitConfigurer();

    assertThrows(
        NullPointerException.class,
        () ->
            httpConduitConfigurer.configure(INSURANCE_PROCESS_NAME, "http://any-address", conduit));

    verify(conduit).setClient(org.mockito.ArgumentMatchers.any(HTTPClientPolicy.class));
  }

  @Test
  void shouldThrowIllegalStateExceptionOnKeystoreFailure() {
    final var mtls =
        new MtlsConfig("/nonexistent.p12", "pw", "PKCS12", "pw", "/nonexistent.jks", "pw", "JKS");
    when(props.mtls()).thenReturn(mtls);

    final var httpConduitConfigurer = configurer.passthroughHttpConduitConfigurer();

    assertThrows(
        IllegalStateException.class,
        () ->
            httpConduitConfigurer.configure(CLINICAL_PROCESS_NAME, "http://any-address", conduit));
  }

  @Test
  void shouldSetClientPolicyWithCorrectSettings() {
    final var httpConduitConfigurer = configurer.passthroughHttpConduitConfigurer();
    final ArgumentCaptor<HTTPClientPolicy> captor = forClass(HTTPClientPolicy.class);

    assertThrows(
        NullPointerException.class,
        () ->
            httpConduitConfigurer.configure(CLINICAL_PROCESS_NAME, "http://any-address", conduit));

    verify(conduit).setClient(captor.capture());
    final var policy = captor.getValue();
    assertFalse(policy.isAllowChunking());
    assertTrue(policy.isAutoRedirect());
    assertEquals(ConnectionType.KEEP_ALIVE, policy.getConnection());
  }
}
