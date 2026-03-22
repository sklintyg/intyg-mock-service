package se.inera.intyg.intygmockservice.storelog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.model.MediaType;
import org.mockserver.verify.VerificationTimes;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import se.inera.intyg.intygmockservice.application.storelog.dto.LogTypeDTO;
import se.inera.intyg.intygmockservice.common.AbstractPassthroughIT;

class StoreLogPassthroughIT extends AbstractPassthroughIT {

  private static final String SOAP_PATH =
      "/services/informationsecurity/auditing/log/StoreLog/v2/rivtabp21";
  private static final String REST_PATH = "/api/store-log";

  private static final String SOAP_OK_RESPONSE =
      """
      <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
          xmlns:ns3="urn:riv:informationsecurity:auditing:log:StoreLogResponder:2"
          xmlns:ns2="urn:riv:informationsecurity:auditing:log:2">
        <soapenv:Header/>
        <soapenv:Body>
          <ns3:StoreLogResponse>
            <ns2:result>
              <ns2:resultCode>OK</ns2:resultCode>
            </ns2:result>
          </ns3:StoreLogResponse>
        </soapenv:Body>
      </soapenv:Envelope>
      """;

  @BeforeEach
  void setUp() {
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
    postSoap(SOAP_PATH, "soap/store-log.xml");

    mockServerClient.verify(
        request().withMethod("POST").withPath(SOAP_PATH), VerificationTimes.exactly(1));
  }

  @Test
  void shouldStoreLocallyEvenWhenPassthroughEnabled() throws IOException {
    postSoap(SOAP_PATH, "soap/store-log.xml");

    final var response = restTemplate.getForEntity(REST_PATH, LogTypeDTO[].class);
    assertTrue(response.getBody().length > 0);
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
        new ClassPathResource("soap/store-log.xml").getContentAsString(StandardCharsets.UTF_8);

    final var soapResponse =
        restTemplate.exchange(
            SOAP_PATH, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);

    assertEquals(200, soapResponse.getStatusCode().value());
    assertTrue(soapResponse.getBody().contains(">ERROR<"));

    final var stored = restTemplate.getForEntity(REST_PATH, LogTypeDTO[].class);
    assertTrue(stored.getBody().length > 0);
  }
}
