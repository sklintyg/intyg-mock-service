package se.inera.intyg.intygmockservice.revokecertificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import se.inera.intyg.intygmockservice.revokecertificate.dto.RevokeCertificateDTO;

@ActiveProfiles("integration-test")
@SpringBootTest(classes = IntygMockServiceApplication.class, webEnvironment = RANDOM_PORT)
class RevokeCertificateIT {

  private static final String SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/RevokeCertificate/2/rivtabp21";
  private static final String REST_PATH = "/api/revoke-certificate";

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void cleanUp() {
    restTemplate.delete(REST_PATH);
  }

  @Test
  void shouldStoreRevokedCertificateViaSoap() throws IOException {
    postSoap("soap/revoke-certificate.xml");

    final var response = restTemplate.getForEntity(REST_PATH, RevokeCertificateDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-revoke-cert-001", items[0].getIntygsId().getExtension());
  }

  @Test
  void shouldReturnEmptyListWhenNoCertificateRevoked() {
    final var response = restTemplate.getForEntity(REST_PATH, RevokeCertificateDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldDeleteAllRevokedCertificates() throws IOException {
    postSoap("soap/revoke-certificate.xml");

    restTemplate.delete(REST_PATH);

    final var response = restTemplate.getForEntity(REST_PATH, RevokeCertificateDTO[].class);
    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldReturnRevokeCertificateById() throws IOException {
    postSoap("soap/revoke-certificate.xml");

    final var response =
        restTemplate.getForEntity(REST_PATH + "/it-revoke-cert-001", RevokeCertificateDTO.class);

    assertEquals(200, response.getStatusCode().value());
    assertEquals("it-revoke-cert-001", response.getBody().getIntygsId().getExtension());
  }

  @Test
  void shouldReturn404WhenRevokeCertificateNotFoundById() {
    final var response =
        restTemplate.getForEntity(REST_PATH + "/unknown-id", RevokeCertificateDTO.class);

    assertEquals(404, response.getStatusCode().value());
  }

  @Test
  void shouldDeleteRevokeCertificateById() throws IOException {
    postSoap("soap/revoke-certificate.xml");
    postSoap("soap/revoke-certificate-2.xml");

    restTemplate.delete(REST_PATH + "/it-revoke-cert-001");

    final var response = restTemplate.getForEntity(REST_PATH, RevokeCertificateDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-revoke-cert-002", items[0].getIntygsId().getExtension());
  }

  @Test
  void shouldReturnRevokeCertificatesByLogicalAddress() throws IOException {
    postSoap("soap/revoke-certificate.xml");
    postSoap("soap/revoke-certificate-2.xml");

    final var response =
        restTemplate.getForEntity(REST_PATH + "/logical-address/FK", RevokeCertificateDTO[].class);

    assertEquals(2, response.getBody().length);
  }

  @Test
  void shouldReturnEmptyListForUnknownLogicalAddress() throws IOException {
    postSoap("soap/revoke-certificate.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/logical-address/UNKNOWN", RevokeCertificateDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldReturnRevokeCertificatesByPersonId() throws IOException {
    postSoap("soap/revoke-certificate.xml");
    postSoap("soap/revoke-certificate-2.xml");

    final var response =
        restTemplate.getForEntity(REST_PATH + "/person/191212121212", RevokeCertificateDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-revoke-cert-001", items[0].getIntygsId().getExtension());
  }

  @Test
  void shouldNormalisePersonIdWithHyphen() throws IOException {
    postSoap("soap/revoke-certificate.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/person/19121212-1212", RevokeCertificateDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-revoke-cert-001", items[0].getIntygsId().getExtension());
  }

  @Test
  void shouldReturnEmptyListForUnknownPersonId() throws IOException {
    postSoap("soap/revoke-certificate.xml");

    final var response =
        restTemplate.getForEntity(REST_PATH + "/person/000000000000", RevokeCertificateDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  private void postSoap(String resourcePath) throws IOException {
    final var resource = new ClassPathResource(resourcePath);
    final var body = resource.getContentAsString(StandardCharsets.UTF_8);

    final var headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");

    restTemplate.exchange(
        SOAP_PATH, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
  }
}
