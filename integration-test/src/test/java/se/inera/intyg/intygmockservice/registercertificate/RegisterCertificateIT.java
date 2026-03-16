package se.inera.intyg.intygmockservice.registercertificate;

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
import se.inera.intyg.intygmockservice.registercertificate.dto.RegisterCertificateDTO;

@ActiveProfiles("integration-test")
@SpringBootTest(classes = IntygMockServiceApplication.class, webEnvironment = RANDOM_PORT)
class RegisterCertificateIT {

  private static final String SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/RegisterCertificate/3/rivtabp21";
  private static final String REST_PATH = "/api/register-certificate";

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void cleanUp() {
    restTemplate.delete(REST_PATH);
  }

  @Test
  void shouldStoreRegisteredCertificateViaSoap() throws IOException {
    postSoap("soap/register-certificate.xml");

    final var response = restTemplate.getForEntity(REST_PATH, RegisterCertificateDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-register-cert-001", items[0].getIntyg().getIntygsId().getExtension());
  }

  @Test
  void shouldReturnEmptyListWhenNoCertificateRegistered() {
    final var response = restTemplate.getForEntity(REST_PATH, RegisterCertificateDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldDeleteAllRegisteredCertificates() throws IOException {
    postSoap("soap/register-certificate.xml");

    restTemplate.delete(REST_PATH);

    final var response = restTemplate.getForEntity(REST_PATH, RegisterCertificateDTO[].class);
    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldReturnCertificateAsXml() throws IOException {
    postSoap("soap/register-certificate.xml");

    final var response =
        restTemplate.getForEntity(REST_PATH + "/it-register-cert-001/xml", String.class);

    assertEquals(
        200, response.getStatusCode().value(), "Expected HTTP 200 for existing certificate");
    assertTrue(
        response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_XML),
        "Expected Content-Type to be application/xml but was: "
            + response.getHeaders().getContentType());

    final var expectedXml =
        new ClassPathResource("expected/register-certificate-xml.xml")
            .getContentAsString(StandardCharsets.UTF_8)
            .replaceAll("\\s", "");
    final var actualXml = response.getBody().replaceAll("\\s", "");

    assertEquals(
        expectedXml, actualXml, "XML response did not match expected XML (whitespace ignored)");
  }

  @Test
  void shouldReturn404WhenCertificateNotFoundAsXml() {
    final var response = restTemplate.getForEntity(REST_PATH + "/nonexistent/xml", String.class);

    assertEquals(
        404, response.getStatusCode().value(), "Expected HTTP 404 for non-existent certificate ID");
  }

  @Test
  void shouldReturnCertificateByIdAsDto() throws IOException {
    postSoap("soap/register-certificate.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/it-register-cert-001", RegisterCertificateDTO.class);

    assertEquals(200, response.getStatusCode().value());
    assertEquals(
        "it-register-cert-001", response.getBody().getIntyg().getIntygsId().getExtension());
  }

  @Test
  void shouldReturn404WhenCertificateNotFoundById() {
    final var response =
        restTemplate.getForEntity(REST_PATH + "/nonexistent", RegisterCertificateDTO.class);

    assertEquals(404, response.getStatusCode().value());
  }

  @Test
  void shouldDeleteCertificateById() throws IOException {
    postSoap("soap/register-certificate.xml");
    postSoap("soap/register-certificate-2.xml");

    restTemplate.delete(REST_PATH + "/it-register-cert-001");

    final var response = restTemplate.getForEntity(REST_PATH, RegisterCertificateDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-register-cert-002", items[0].getIntyg().getIntygsId().getExtension());
  }

  @Test
  void shouldReturnCertificatesByLogicalAddress() throws IOException {
    postSoap("soap/register-certificate.xml");
    postSoap("soap/register-certificate-2.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/logical-address/FK", RegisterCertificateDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-register-cert-001", items[0].getIntyg().getIntygsId().getExtension());
  }

  @Test
  void shouldReturnEmptyListForUnknownLogicalAddress() throws IOException {
    postSoap("soap/register-certificate.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/logical-address/UNKNOWN", RegisterCertificateDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldReturnCertificatesByPersonId() throws IOException {
    postSoap("soap/register-certificate.xml");
    postSoap("soap/register-certificate-2.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/person/191212121212", RegisterCertificateDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-register-cert-001", items[0].getIntyg().getIntygsId().getExtension());
  }

  @Test
  void shouldNormalisePersonIdWithHyphen() throws IOException {
    postSoap("soap/register-certificate.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/person/19121212-1212", RegisterCertificateDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-register-cert-001", items[0].getIntyg().getIntygsId().getExtension());
  }

  @Test
  void shouldReturnEmptyListForUnknownPersonId() throws IOException {
    postSoap("soap/register-certificate.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/person/000000000000", RegisterCertificateDTO[].class);

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
