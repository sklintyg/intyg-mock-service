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
class RevocationNavigationIT {

  private static final String REVOKE_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/RevokeCertificate/2/rivtabp21";

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void cleanUp() {
    restTemplate.delete("/api/reset");
  }

  @Test
  void shouldReturn404ForUnknownCertificateId() {
    final var response =
        restTemplate.getForEntity("/api/navigate/certificates/unknown/revocation", String.class);

    assertEquals(404, response.getStatusCode().value());
  }

  @Test
  void shouldReturnRevocationByIdWithHalLinks() throws IOException {
    postSoap(REVOKE_SOAP_PATH, "soap/revoke-certificate.xml");

    final var response =
        restTemplate.getForEntity(
            "/api/navigate/certificates/it-revoke-cert-001/revocation", String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    assertTrue(body.contains("it-revoke-cert-001"), "Response should contain certificate ID");
    assertTrue(body.contains("191212121212"), "Response should contain person ID");
    assertTrue(body.contains("\"_links\""), "Response should have HAL _links");
    assertTrue(body.contains("\"self\""), "Response should have self link");
    assertTrue(body.contains("\"certificate\""), "Response should have certificate link");
    assertTrue(body.contains("\"patient\""), "Response should have patient link");
  }

  @Test
  void shouldReturnRevocationWithReason() throws IOException {
    postSoap(REVOKE_SOAP_PATH, "soap/revoke-certificate.xml");

    final var response =
        restTemplate.getForEntity(
            "/api/navigate/certificates/it-revoke-cert-001/revocation", String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    assertTrue(body.contains("Revoked for integration test"), "Response should contain reason");
  }

  @Test
  void shouldLinkToCorrectCertificateUrl() throws IOException {
    postSoap(REVOKE_SOAP_PATH, "soap/revoke-certificate.xml");

    final var response =
        restTemplate.getForEntity(
            "/api/navigate/certificates/it-revoke-cert-001/revocation", String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    assertTrue(
        body.contains("/api/navigate/certificates/it-revoke-cert-001"),
        "Response should link to certificate");
    assertTrue(
        body.contains("/api/navigate/patients/191212121212"), "Response should link to patient");
  }

  private void postSoap(final String soapPath, final String resourcePath) throws IOException {
    final var body = new ClassPathResource(resourcePath).getContentAsString(StandardCharsets.UTF_8);
    final var headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");
    restTemplate.exchange(soapPath, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
  }
}
