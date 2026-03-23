package se.inera.intyg.intygmockservice.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import se.inera.intyg.intygmockservice.IntygMockServiceApplication;

@ActiveProfiles("integration-test")
@SpringBootTest(classes = IntygMockServiceApplication.class, webEnvironment = RANDOM_PORT)
class LogEntryNavigationIT {

  private static final String STORE_LOG_SOAP_PATH =
      "/services/informationsecurity/auditing/log/StoreLog/v2/rivtabp21";

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void cleanUp() {
    restTemplate.delete("/api/reset");
  }

  @Test
  void shouldReturnEmptyLogEntriesWhenNoneStored() {
    final var response = restTemplate.getForEntity("/api/navigate/log-entries", String.class);

    assertEquals(200, response.getStatusCode().value());
    assertTrue(response.getBody().contains("\"_links\""), "Response should have HAL _links");
  }

  @Test
  void shouldReturnAllLogEntriesWithHalLinks() throws IOException {
    postSoap(STORE_LOG_SOAP_PATH, "soap/store-log-cert.xml");

    final var response = restTemplate.getForEntity("/api/navigate/log-entries", String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    assertTrue(body.contains("it-log-cert-001"), "Response should contain log ID");
    assertTrue(body.contains("WEBCERT"), "Response should contain system ID");
    assertTrue(body.contains("\"_links\""), "Response should have HAL _links");
    assertTrue(body.contains("\"self\""), "Response should have self link");
  }

  @Test
  void shouldReturnLogEntryWithCertificateLinkWhenCertificateIdPresent() throws IOException {
    postSoap(STORE_LOG_SOAP_PATH, "soap/store-log-cert.xml");

    final var response = restTemplate.getForEntity("/api/navigate/log-entries", String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    assertTrue(body.contains("\"certificate\""), "Response should have certificate link");
    assertTrue(
        body.contains("/api/navigate/certificates/it-log-cert-001"),
        "Response should link to certificate");
  }

  @Test
  void shouldReturnLogEntriesForCertificate() throws IOException {
    postSoap(STORE_LOG_SOAP_PATH, "soap/store-log-cert.xml");

    final var response =
        restTemplate.getForEntity(
            "/api/navigate/certificates/it-log-cert-001/log-entries", String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    assertTrue(body.contains("it-log-cert-001"), "Response should contain log ID");
    assertTrue(body.contains("\"_links\""), "Response should have HAL _links");
  }

  @Test
  void shouldReturnEmptyCollectionForUnknownCertificate() {
    final var response =
        restTemplate.getForEntity("/api/navigate/certificates/unknown/log-entries", String.class);

    assertEquals(200, response.getStatusCode().value());
  }

  @Test
  void shouldReturnLogEntryFieldsCorrectly() throws IOException {
    postSoap(STORE_LOG_SOAP_PATH, "soap/store-log-cert.xml");

    final var response = restTemplate.getForEntity("/api/navigate/log-entries", String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    assertTrue(body.contains("CARE_TREATMENT"), "Response should contain purpose");
    assertTrue(body.contains("it-user-001"), "Response should contain user ID");
    assertTrue(body.contains("Alfa Regionen"), "Response should contain care provider name");
    assertTrue(body.contains("TSTNMT2321000156-ALMC"), "Response should contain care unit ID");
  }

  private void postSoap(final String soapPath, final String resourcePath) throws IOException {
    final var body = new ClassPathResource(resourcePath).getContentAsString(StandardCharsets.UTF_8);
    final var headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");
    restTemplate.exchange(soapPath, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
  }
}
