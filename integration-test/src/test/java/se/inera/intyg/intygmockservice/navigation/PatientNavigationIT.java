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
class PatientNavigationIT {

  private static final String REGISTER_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/RegisterCertificate/3/rivtabp21";
  private static final String NAV_PATH = "/api/navigate/patients";

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void cleanUp() {
    restTemplate.delete("/api/reset");
  }

  @Test
  void shouldReturn404ForUnknownPersonId() {
    final var response = restTemplate.getForEntity(NAV_PATH + "/unknown", String.class);

    assertEquals(404, response.getStatusCode().value());
  }

  @Test
  void shouldReturnPatientWithHalLinksAfterRegisterCertificate() throws IOException {
    postSoap(REGISTER_SOAP_PATH, "soap/register-certificate.xml");

    final var response = restTemplate.getForEntity(NAV_PATH + "/191212121212", String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    assertTrue(body.contains("191212121212"), "Response should contain person ID");
    assertTrue(body.contains("\"_links\""), "Response should have HAL _links");
    assertTrue(body.contains("\"self\""), "Response should have self link");
    assertTrue(body.contains("\"certificates\""), "Response should have certificates link");
    assertTrue(body.contains("\"messages\""), "Response should have messages link");
  }

  @Test
  void shouldReturnCertificatesForPatient() throws IOException {
    postSoap(REGISTER_SOAP_PATH, "soap/register-certificate.xml");

    final var response =
        restTemplate.getForEntity(NAV_PATH + "/191212121212/certificates", String.class);
    final var body = response.getBody();

    assertEquals(200, response.getStatusCode().value());
    assertTrue(body.contains("it-register-cert-001"), "Response should contain certificate ID");
  }

  @Test
  void shouldReturnEmptyMessagesStub() throws IOException {
    postSoap(REGISTER_SOAP_PATH, "soap/register-certificate.xml");

    final var response =
        restTemplate.getForEntity(NAV_PATH + "/191212121212/messages", String.class);

    assertEquals(200, response.getStatusCode().value());
  }

  private void postSoap(final String soapPath, final String resourcePath) throws IOException {
    final var body = new ClassPathResource(resourcePath).getContentAsString(StandardCharsets.UTF_8);
    final var headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");
    restTemplate.exchange(soapPath, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
  }
}
