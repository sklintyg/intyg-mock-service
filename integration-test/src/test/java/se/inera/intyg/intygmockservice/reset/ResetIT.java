package se.inera.intyg.intygmockservice.reset;

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

@ActiveProfiles("integration-test")
@SpringBootTest(classes = IntygMockServiceApplication.class, webEnvironment = RANDOM_PORT)
class ResetIT {

  private static final String RESET_PATH = "/api/reset";

  private static final String REGISTER_CERTIFICATE_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/RegisterCertificate/3/rivtabp21";
  private static final String REVOKE_CERTIFICATE_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/RevokeCertificate/2/rivtabp21";
  private static final String SEND_MESSAGE_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/SendMessageToRecipient/2/rivtabp21";
  private static final String STATUS_UPDATE_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/CertificateStatusUpdateForCare/3/rivtabp21";
  private static final String STORE_LOG_SOAP_PATH =
      "/services/informationsecurity/auditing/log/StoreLog/v2/rivtabp21";

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void cleanUp() {
    restTemplate.delete(RESET_PATH);
  }

  @Test
  void shouldDeleteAllDataAcrossAllModules() throws IOException {
    postSoap(REGISTER_CERTIFICATE_SOAP_PATH, "soap/register-certificate.xml");
    postSoap(REVOKE_CERTIFICATE_SOAP_PATH, "soap/revoke-certificate.xml");
    postSoap(SEND_MESSAGE_SOAP_PATH, "soap/send-message-to-recipient.xml");
    postSoap(STATUS_UPDATE_SOAP_PATH, "soap/certificate-status-update-for-care.xml");
    postSoap(STORE_LOG_SOAP_PATH, "soap/store-log.xml");

    restTemplate.delete(RESET_PATH);

    assertEquals(0, getCount("/api/register-certificate/count"));
    assertEquals(0, getCount("/api/revoke-certificate/count"));
    assertEquals(0, getCount("/api/send-message-to-recipient/count"));
    assertEquals(0, getCount("/api/certificate-status-for-care/count"));
    assertEquals(0, getCount("/api/store-log/count"));
  }

  private int getCount(String path) {
    return restTemplate.getForEntity(path, CountResponse.class).getBody().count();
  }

  private void postSoap(String soapPath, String resourcePath) throws IOException {
    final var resource = new ClassPathResource(resourcePath);
    final var body = resource.getContentAsString(StandardCharsets.UTF_8);

    final var headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");

    restTemplate.exchange(soapPath, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
  }
}
