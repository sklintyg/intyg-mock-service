package se.inera.intyg.intygmockservice.sendmessagetorecipient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.mockserver.verify.VerificationTimes;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import se.inera.intyg.intygmockservice.IntygMockServiceApplication;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.dto.SendMessageToRecipientDTO;

@Testcontainers
@ActiveProfiles("integration-test")
@SpringBootTest(classes = IntygMockServiceApplication.class, webEnvironment = RANDOM_PORT)
class SendMessageToRecipientPassthroughIT {

  private static final String SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/SendMessageToRecipient/2/rivtabp21";
  private static final String REST_PATH = "/api/send-message-to-recipient";

  private static final String SOAP_OK_RESPONSE =
      """
      <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
          xmlns:ns5="urn:riv:clinicalprocess:healthcond:certificate:SendMessageToRecipientResponder:2"
          xmlns:ns2="urn:riv:clinicalprocess:healthcond:certificate:v3">
        <soapenv:Header/>
        <soapenv:Body>
          <ns5:SendMessageToRecipientResponse>
            <ns2:result>
              <ns2:resultCode>OK</ns2:resultCode>
            </ns2:result>
          </ns5:SendMessageToRecipientResponse>
        </soapenv:Body>
      </soapenv:Envelope>
      """;

  @Container
  static final MockServerContainer mockServer =
      new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.15.0"));

  static MockServerClient mockServerClient;

  @DynamicPropertySource
  static void configurePassthrough(DynamicPropertyRegistry registry) {
    registry.add("app.passthrough.send-message-to-recipient.enabled", () -> "true");
    registry.add(
        "app.passthrough.send-message-to-recipient.url",
        () -> "http://" + mockServer.getHost() + ":" + mockServer.getServerPort() + SOAP_PATH);
  }

  @BeforeAll
  static void initMockServerClient() {
    mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
  }

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void setUp() {
    restTemplate.delete(REST_PATH);
    mockServerClient.reset();
    mockServerClient
        .when(request().withMethod("POST").withPath(SOAP_PATH))
        .respond(
            response()
                .withStatusCode(200)
                .withContentType(MediaType.TEXT_XML_UTF_8)
                .withBody(SOAP_OK_RESPONSE));
  }

  @Test
  void shouldForwardToUpstreamAfterStoringLocally() throws IOException {
    postSoap("soap/send-message-to-recipient.xml");

    mockServerClient.verify(
        request().withMethod("POST").withPath(SOAP_PATH), VerificationTimes.exactly(1));
  }

  @Test
  void shouldStoreLocallyEvenWhenPassthroughEnabled() throws IOException {
    postSoap("soap/send-message-to-recipient.xml");

    final var response = restTemplate.getForEntity(REST_PATH, SendMessageToRecipientDTO[].class);
    assertEquals(1, response.getBody().length);
  }

  @Test
  void shouldReturnErrorWhenUpstreamFails() throws IOException {
    mockServerClient.reset();
    mockServerClient
        .when(request().withMethod("POST").withPath(SOAP_PATH))
        .respond(response().withStatusCode(500));

    final var headers = new HttpHeaders();
    headers.setContentType(org.springframework.http.MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");
    final var body =
        new ClassPathResource("soap/send-message-to-recipient.xml")
            .getContentAsString(StandardCharsets.UTF_8);

    final var soapResponse =
        restTemplate.exchange(
            SOAP_PATH, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);

    assertEquals(200, soapResponse.getStatusCode().value());
    assertTrue(soapResponse.getBody().contains(">ERROR<"));

    final var stored = restTemplate.getForEntity(REST_PATH, SendMessageToRecipientDTO[].class);
    assertEquals(1, stored.getBody().length);
  }

  private void postSoap(String resourcePath) throws IOException {
    final var body = new ClassPathResource(resourcePath).getContentAsString(StandardCharsets.UTF_8);
    final var headers = new HttpHeaders();
    headers.setContentType(org.springframework.http.MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");
    restTemplate.exchange(
        SOAP_PATH, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
  }
}
