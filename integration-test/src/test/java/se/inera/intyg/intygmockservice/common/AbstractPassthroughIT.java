package se.inera.intyg.intygmockservice.common;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;
import se.inera.intyg.intygmockservice.IntygMockServiceApplication;

@ActiveProfiles("integration-test")
@SpringBootTest(classes = IntygMockServiceApplication.class, webEnvironment = RANDOM_PORT)
public abstract class AbstractPassthroughIT {

  private static final String REGISTER_CERTIFICATE_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/RegisterCertificate/3/rivtabp21";
  private static final String REVOKE_CERTIFICATE_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/RevokeCertificate/2/rivtabp21";
  private static final String SEND_MESSAGE_TO_RECIPIENT_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/SendMessageToRecipient/2/rivtabp21";
  private static final String CERTIFICATE_STATUS_UPDATE_FOR_CARE_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/CertificateStatusUpdateForCare/3/rivtabp21";
  private static final String STORE_LOG_SOAP_PATH =
      "/services/informationsecurity/auditing/log/StoreLog/v2/rivtabp21";

  static final MockServerContainer mockServer;
  protected static final MockServerClient mockServerClient;

  static {
    mockServer = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.15.0"));
    mockServer.start();
    mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
  }

  @DynamicPropertySource
  static void configurePassthrough(DynamicPropertyRegistry registry) {
    registry.add("app.passthrough.register-certificate.enabled", () -> "true");
    registry.add(
        "app.passthrough.register-certificate.url",
        () ->
            "http://"
                + mockServer.getHost()
                + ":"
                + mockServer.getServerPort()
                + REGISTER_CERTIFICATE_SOAP_PATH);

    registry.add("app.passthrough.revoke-certificate.enabled", () -> "true");
    registry.add(
        "app.passthrough.revoke-certificate.url",
        () ->
            "http://"
                + mockServer.getHost()
                + ":"
                + mockServer.getServerPort()
                + REVOKE_CERTIFICATE_SOAP_PATH);

    registry.add("app.passthrough.send-message-to-recipient.enabled", () -> "true");
    registry.add(
        "app.passthrough.send-message-to-recipient.url",
        () ->
            "http://"
                + mockServer.getHost()
                + ":"
                + mockServer.getServerPort()
                + SEND_MESSAGE_TO_RECIPIENT_SOAP_PATH);

    registry.add("app.passthrough.certificate-status-update-for-care.enabled", () -> "true");
    registry.add(
        "app.passthrough.certificate-status-update-for-care.url",
        () ->
            "http://"
                + mockServer.getHost()
                + ":"
                + mockServer.getServerPort()
                + CERTIFICATE_STATUS_UPDATE_FOR_CARE_SOAP_PATH);

    registry.add("app.passthrough.store-log.enabled", () -> "true");
    registry.add(
        "app.passthrough.store-log.url",
        () ->
            "http://"
                + mockServer.getHost()
                + ":"
                + mockServer.getServerPort()
                + STORE_LOG_SOAP_PATH);
  }

  @Autowired protected TestRestTemplate restTemplate;

  @BeforeEach
  void resetAll() {
    restTemplate.delete("/api/reset");
    mockServerClient.reset();
  }

  protected void postSoap(String soapPath, String resourcePath) throws IOException {
    final var body = new ClassPathResource(resourcePath).getContentAsString(StandardCharsets.UTF_8);
    final var headers = new HttpHeaders();
    headers.setContentType(org.springframework.http.MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");
    restTemplate.exchange(soapPath, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
  }
}
