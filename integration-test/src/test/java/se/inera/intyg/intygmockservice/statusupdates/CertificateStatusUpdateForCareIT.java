package se.inera.intyg.intygmockservice.statusupdates;

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
import se.inera.intyg.intygmockservice.common.dto.CountResponse;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO;

@ActiveProfiles("integration-test")
@SpringBootTest(classes = IntygMockServiceApplication.class, webEnvironment = RANDOM_PORT)
class CertificateStatusUpdateForCareIT {

  private static final String SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/CertificateStatusUpdateForCare/3/rivtabp21";
  private static final String REST_PATH = "/api/certificate-status-for-care";

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void cleanUp() {
    restTemplate.delete(REST_PATH);
  }

  @Test
  void shouldStoreStatusUpdateViaSoap() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");

    final var response =
        restTemplate.getForEntity(REST_PATH, CertificateStatusUpdateForCareDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-status-update-001", items[0].getIntyg().getIntygsId().getExtension());
  }

  @Test
  void shouldReturnEmptyListWhenNoStatusUpdateReceived() {
    final var response =
        restTemplate.getForEntity(REST_PATH, CertificateStatusUpdateForCareDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldDeleteAllStatusUpdates() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");

    restTemplate.delete(REST_PATH);

    final var response =
        restTemplate.getForEntity(REST_PATH, CertificateStatusUpdateForCareDTO[].class);
    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldReturnStatusUpdatesByCertificateId() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");
    postSoap("soap/certificate-status-update-for-care-2.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/it-status-update-001", CertificateStatusUpdateForCareDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-status-update-001", items[0].getIntyg().getIntygsId().getExtension());
  }

  @Test
  void shouldReturnEmptyListForUnknownCertificateId() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/unknown-cert-id", CertificateStatusUpdateForCareDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldDeleteStatusUpdatesByCertificateId() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");
    postSoap("soap/certificate-status-update-for-care-2.xml");

    restTemplate.delete(REST_PATH + "/it-status-update-001");

    final var response =
        restTemplate.getForEntity(REST_PATH, CertificateStatusUpdateForCareDTO[].class);
    assertEquals(1, response.getBody().length);
    assertEquals(
        "it-status-update-002", response.getBody()[0].getIntyg().getIntygsId().getExtension());
  }

  @Test
  void shouldReturnStatusUpdatesByLogicalAddress() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");
    postSoap("soap/certificate-status-update-for-care-2.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/logical-address/TSTNMT2321000156-ALMC",
            CertificateStatusUpdateForCareDTO[].class);

    assertEquals(2, response.getBody().length);
  }

  @Test
  void shouldReturnEmptyListForUnknownLogicalAddress() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/logical-address/UNKNOWN-ADDRESS",
            CertificateStatusUpdateForCareDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldReturnStatusUpdatesByPersonId() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");
    postSoap("soap/certificate-status-update-for-care-2.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/person/191212121212", CertificateStatusUpdateForCareDTO[].class);

    assertEquals(1, response.getBody().length);
    assertEquals(
        "it-status-update-001", response.getBody()[0].getIntyg().getIntygsId().getExtension());
  }

  @Test
  void shouldNormalisePersonIdWithHyphen() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/person/19121212-1212", CertificateStatusUpdateForCareDTO[].class);

    assertEquals(1, response.getBody().length);
  }

  @Test
  void shouldReturnEmptyListForUnknownPersonId() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/person/000000000000", CertificateStatusUpdateForCareDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldReturnStatusUpdatesByEventCodeSkapat() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");
    postSoap("soap/certificate-status-update-for-care-2.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/event-type/SKAPAT", CertificateStatusUpdateForCareDTO[].class);

    assertEquals(1, response.getBody().length);
    assertEquals(
        "it-status-update-001", response.getBody()[0].getIntyg().getIntygsId().getExtension());
  }

  @Test
  void shouldReturnStatusUpdatesByEventCodeSkicka() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");
    postSoap("soap/certificate-status-update-for-care-2.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/event-type/SKICKA", CertificateStatusUpdateForCareDTO[].class);

    assertEquals(1, response.getBody().length);
    assertEquals(
        "it-status-update-002", response.getBody()[0].getIntyg().getIntygsId().getExtension());
  }

  @Test
  void shouldReturnEmptyListForUnknownEventCode() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/event-type/UNKNOWN", CertificateStatusUpdateForCareDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldReturnZeroCountWhenEmpty() {
    final var response = restTemplate.getForEntity(REST_PATH + "/count", CountResponse.class);

    assertEquals(0, response.getBody().count());
  }

  @Test
  void shouldReturnCountMatchingStoredCalls() throws IOException {
    postSoap("soap/certificate-status-update-for-care.xml");
    postSoap("soap/certificate-status-update-for-care-2.xml");

    final var response = restTemplate.getForEntity(REST_PATH + "/count", CountResponse.class);

    assertEquals(2, response.getBody().count());
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
