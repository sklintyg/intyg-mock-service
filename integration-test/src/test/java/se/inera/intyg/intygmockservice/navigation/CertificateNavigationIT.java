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
class CertificateNavigationIT {

  private static final String REGISTER_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/RegisterCertificate/3/rivtabp21";
  private static final String REVOKE_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/RevokeCertificate/2/rivtabp21";
  private static final String NAV_PATH = "/api/navigate/certificates";

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void cleanUp() {
    restTemplate.delete("/api/reset");
  }

  @Test
  void shouldReturnEmptyCollectionWhenNoData() {
    final var response = restTemplate.getForEntity(NAV_PATH, String.class);

    assertEquals(200, response.getStatusCode().value());
    final var body = response.getBody();
    // HAL empty collection has no _embedded
    assertTrue(body.contains("\"_links\""));
  }

  @Test
  void shouldReturnRegisteredCertificateWithHalLinks() throws IOException {
    postSoap(REGISTER_SOAP_PATH, "soap/register-certificate.xml");

    final var response = restTemplate.getForEntity(NAV_PATH, String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    assertTrue(body.contains("it-register-cert-001"), "Response should contain certificate ID");
    assertTrue(body.contains("\"_links\""), "Response should have HAL _links");
    assertTrue(body.contains("\"self\""), "Response should have self link");
    assertTrue(body.contains("\"messages\""), "Response should have messages link");
    assertTrue(body.contains("\"status-updates\""), "Response should have status-updates link");
    assertTrue(body.contains("\"log-entries\""), "Response should have log-entries link");
    assertTrue(body.contains("\"revocation\""), "Response should have revocation link");
  }

  @Test
  void shouldReturnMergedViewIncludingRevocationOnlyCertificate() throws IOException {
    postSoap(REGISTER_SOAP_PATH, "soap/register-certificate.xml");
    postSoap(REVOKE_SOAP_PATH, "soap/revoke-certificate.xml");

    final var response = restTemplate.getForEntity(NAV_PATH, String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    // Both the registered cert and the revoked cert (different ID) should appear
    assertTrue(body.contains("it-register-cert-001"), "Should contain registered certificate");
    assertTrue(body.contains("it-revoke-cert-001"), "Should contain revocation-only certificate");
  }

  @Test
  void shouldReturnTwoCertificatesFromMergedViewWhenRegisterAndRevokeHaveDifferentIds()
      throws IOException {
    postSoap(REGISTER_SOAP_PATH, "soap/register-certificate.xml"); // it-register-cert-001
    postSoap(REVOKE_SOAP_PATH, "soap/revoke-certificate.xml"); // it-revoke-cert-001 (different ID)

    final var response = restTemplate.getForEntity(NAV_PATH, String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    // Both unique cert IDs should appear in the merged collection
    assertTrue(body.contains("it-register-cert-001"));
    assertTrue(body.contains("it-revoke-cert-001"));
    // Two "certificateId" field occurrences means two distinct certificate objects
    final var count = countOccurrences(body, "\"certificateId\":");
    assertEquals(2, count, "Merged view should contain exactly 2 distinct certificates");
  }

  @Test
  void shouldReturnCertificateByIdWithAllLinks() throws IOException {
    postSoap(REGISTER_SOAP_PATH, "soap/register-certificate.xml");

    final var response =
        restTemplate.getForEntity(NAV_PATH + "/it-register-cert-001", String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    assertTrue(body.contains("it-register-cert-001"));
    assertTrue(body.contains("\"self\""));
    assertTrue(body.contains("\"patient\""), "Should include patient link when personId present");
    assertTrue(body.contains("\"messages\""));
    assertTrue(body.contains("\"revocation\""));
  }

  @Test
  void shouldReturn404ForUnknownCertificateId() {
    final var response = restTemplate.getForEntity(NAV_PATH + "/unknown-cert", String.class);

    assertEquals(404, response.getStatusCode().value());
  }

  @Test
  void shouldIncludeFullPatientDataInCertificateResponse() throws IOException {
    postSoap(REGISTER_SOAP_PATH, "soap/register-certificate.xml");

    final var response =
        restTemplate.getForEntity(NAV_PATH + "/it-register-cert-001", String.class);
    final var body = response.getBody();

    // The registered certificate contains patient info
    assertTrue(body.contains("\"patient\""));
    assertTrue(body.contains("191212121212"), "Should contain normalized person ID");
  }

  @Test
  void shouldIncludeStaffAndUnitDataInCertificateResponse() throws IOException {
    postSoap(REGISTER_SOAP_PATH, "soap/register-certificate.xml");

    final var response =
        restTemplate.getForEntity(NAV_PATH + "/it-register-cert-001", String.class);
    final var body = response.getBody();

    assertTrue(body.contains("\"issuedBy\""), "Should contain issuer info");
    assertTrue(body.contains("\"unit\""), "Should contain unit info");
    // Links for unit and issuer staff should be present
    assertTrue(body.contains("\"unit\""), "Should include unit link");
    assertTrue(body.contains("\"issuer\""), "Should include issuer link");
  }

  private static int countOccurrences(final String text, final String target) {
    int count = 0;
    int idx = 0;
    while ((idx = text.indexOf(target, idx)) != -1) {
      count++;
      idx += target.length();
    }
    return count;
  }

  private void postSoap(final String soapPath, final String resourcePath) throws IOException {
    final var body = new ClassPathResource(resourcePath).getContentAsString(StandardCharsets.UTF_8);
    final var headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");
    restTemplate.exchange(soapPath, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
  }
}
