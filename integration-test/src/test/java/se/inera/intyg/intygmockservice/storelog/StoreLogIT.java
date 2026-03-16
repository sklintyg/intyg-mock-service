package se.inera.intyg.intygmockservice.storelog;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO;

@ActiveProfiles("integration-test")
@SpringBootTest(classes = IntygMockServiceApplication.class, webEnvironment = RANDOM_PORT)
class StoreLogIT {

  private static final String SOAP_PATH =
      "/services/informationsecurity/auditing/log/StoreLog/v2/rivtabp21";
  private static final String REST_PATH = "/api/store-log";

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void cleanUp() {
    restTemplate.delete(REST_PATH);
  }

  @Test
  void shouldStoreLogViaSoap() throws IOException {
    postSoap("soap/store-log.xml");

    final var response = restTemplate.getForEntity(REST_PATH, LogTypeDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-log-001", items[0].getLogId());
  }

  @Test
  void shouldStoreAllFieldsMatchingSoapMessage() throws IOException {
    postSoap("soap/store-log.xml");

    final var log = restTemplate.getForEntity(REST_PATH, LogTypeDTO[].class).getBody()[0];
    final var resource = log.getResources().get(0);

    assertAll(
        () -> assertEquals("it-log-001", log.getLogId()),
        () -> assertEquals("WEBCERT", log.getSystem().getSystemId()),
        () -> assertEquals("Webcert", log.getSystem().getSystemName()),
        () -> assertEquals("Läsa", log.getActivity().getActivityType()),
        () -> assertEquals("Enhet", log.getActivity().getActivityLevel()),
        () -> assertNull(log.getActivity().getActivityArgs()),
        () ->
            assertEquals(
                LocalDateTime.of(2024, 11, 9, 7, 40, 13), log.getActivity().getStartDate()),
        () -> assertNull(log.getActivity().getPurpose()),
        () -> assertEquals("it-user-001", log.getUser().getUserId()),
        () -> assertEquals("Läkare", log.getUser().getAssignment()),
        () ->
            assertEquals(
                "TSTNMT2321000156-ALFA", log.getUser().getCareProvider().getCareProviderId()),
        () -> assertEquals("Alfa Regionen", log.getUser().getCareProvider().getCareProviderName()),
        () -> assertEquals("TSTNMT2321000156-ALMC", log.getUser().getCareUnit().getCareUnitId()),
        () -> assertEquals("Alfa Medicincentrum", log.getUser().getCareUnit().getCareUnitName()),
        () -> assertEquals(1, log.getResources().size()),
        () -> assertEquals("Intyg", resource.getResourceType()),
        () -> assertEquals("1.2.752.129.2.1.3.1", resource.getPatient().getRoot()),
        () -> assertEquals("191212121212", resource.getPatient().getExtension()),
        () -> assertEquals("TSTNMT2321000156-ALFA", resource.getCareProvider().getCareProviderId()),
        () -> assertEquals("Alfa Regionen", resource.getCareProvider().getCareProviderName()),
        () -> assertEquals("TSTNMT2321000156-ALMC", resource.getCareUnit().getCareUnitId()),
        () -> assertEquals("Alfa Medicincentrum", resource.getCareUnit().getCareUnitName()));
  }

  @Test
  void shouldReturnEmptyListWhenNoLogStored() {
    final var response = restTemplate.getForEntity(REST_PATH, LogTypeDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldDeleteAllLogs() throws IOException {
    postSoap("soap/store-log.xml");

    restTemplate.delete(REST_PATH);

    final var response = restTemplate.getForEntity(REST_PATH, LogTypeDTO[].class);
    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldReturnLogsByUserId() throws IOException {
    postSoap("soap/store-log.xml");

    final var response =
        restTemplate.getForEntity(REST_PATH + "/user/it-user-001", LogTypeDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-user-001", items[0].getUser().getUserId());
  }

  @Test
  void shouldReturnEmptyListForUnknownUserId() throws IOException {
    postSoap("soap/store-log.xml");

    final var response =
        restTemplate.getForEntity(REST_PATH + "/user/unknown-user", LogTypeDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldReturnLogsByCertificateId() throws IOException {
    postSoap("soap/store-log.xml");

    final var response =
        restTemplate.getForEntity(REST_PATH + "/certificate/Enhet", LogTypeDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("Enhet", items[0].getActivity().getActivityLevel());
  }

  @Test
  void shouldReturnEmptyListForUnknownCertificateId() throws IOException {
    postSoap("soap/store-log.xml");

    final var response =
        restTemplate.getForEntity(REST_PATH + "/certificate/unknown-cert", LogTypeDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldDeleteLogsByUserId() throws IOException {
    postSoap("soap/store-log.xml");
    postSoap("soap/store-log-2.xml");

    restTemplate.delete(REST_PATH + "/user/it-user-001");

    final var response = restTemplate.getForEntity(REST_PATH, LogTypeDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-user-002", items[0].getUser().getUserId());
  }

  @Test
  void shouldDeleteLogsByCertificateId() throws IOException {
    postSoap("soap/store-log.xml");
    postSoap("soap/store-log-2.xml");

    restTemplate.delete(REST_PATH + "/certificate/Enhet");

    final var response = restTemplate.getForEntity(REST_PATH, LogTypeDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("Avdelning", items[0].getActivity().getActivityLevel());
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
