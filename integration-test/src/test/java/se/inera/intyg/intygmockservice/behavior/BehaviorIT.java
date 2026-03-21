package se.inera.intyg.intygmockservice.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import se.inera.intyg.intygmockservice.IntygMockServiceApplication;
import se.inera.intyg.intygmockservice.application.common.dto.CountResponse;
import se.inera.intyg.intygmockservice.domain.BehaviorRule;

@ActiveProfiles("integration-test")
@SpringBootTest(classes = IntygMockServiceApplication.class, webEnvironment = RANDOM_PORT)
class BehaviorIT {

  private static final String SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/RegisterCertificate/3/rivtabp21";
  private static final String REVOKE_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/RevokeCertificate/2/rivtabp21";
  private static final String SEND_MESSAGE_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/SendMessageToRecipient/2/rivtabp21";
  private static final String CERTIFICATE_STATUS_SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/CertificateStatusUpdateForCare/3/rivtabp21";
  private static final String BEHAVIOR_PATH = "/api/behavior";
  private static final String REGISTER_CERT_PATH = "/api/register-certificate";
  private static final String REVOKE_CERT_PATH = "/api/revoke-certificate";
  private static final String SEND_MESSAGE_PATH = "/api/send-message-to-recipient";
  private static final String CERTIFICATE_STATUS_PATH = "/api/certificate-status-for-care";

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void cleanUp() {
    restTemplate.delete("/api/reset");
  }

  @Test
  void errorRuleShouldReturnErrorAndNotStoreCertificate() throws IOException {
    createErrorRule(null);

    final var soapResponse = postSoap("soap/register-certificate.xml");

    assertTrue(soapResponse.contains("ERROR"), "Expected ERROR in SOAP response");
    final var count =
        restTemplate.getForEntity(REGISTER_CERT_PATH + "/count", CountResponse.class).getBody();
    assertEquals(0, count.count());
  }

  @Test
  void maxTriggerCountOneShouldFireOnceAndThenPassThrough() throws IOException {
    createErrorRuleWithMaxTriggerCount(1);

    final var firstResponse = postSoap("soap/register-certificate.xml");
    assertTrue(firstResponse.contains("ERROR"), "First call should return ERROR");

    final var secondResponse = postSoap("soap/register-certificate.xml");
    assertTrue(secondResponse.contains("OK"), "Second call should return OK");

    final var count =
        restTemplate.getForEntity(REGISTER_CERT_PATH + "/count", CountResponse.class).getBody();
    assertEquals(1, count.count());
  }

  @Test
  void resetShouldClearBehaviorRules() {
    createErrorRule(null);

    restTemplate.delete("/api/reset");

    final var rules = restTemplate.getForEntity(BEHAVIOR_PATH, BehaviorRule[].class).getBody();
    assertEquals(0, rules.length);
  }

  @Test
  void delayOnlyRuleShouldStoreCertificateAndReturnOk() throws IOException {
    createDelayOnlyRule(10L);

    final var soapResponse = postSoap("soap/register-certificate.xml");

    assertTrue(soapResponse.contains("OK"), "Expected OK in SOAP response");
    final var count =
        restTemplate.getForEntity(REGISTER_CERT_PATH + "/count", CountResponse.class).getBody();
    assertEquals(1, count.count());
  }

  @Test
  void certificateIdSpecificRuleShouldOnlyBlockMatchingCertificate() throws IOException {
    createErrorRule("it-register-cert-001");

    postSoap("soap/register-certificate.xml");
    postSoap("soap/register-certificate-2.xml");

    final var count =
        restTemplate.getForEntity(REGISTER_CERT_PATH + "/count", CountResponse.class).getBody();
    assertEquals(1, count.count());
  }

  @Test
  void errorRuleForRevokeCertificateShouldReturnErrorAndNotStoreRevoke() throws IOException {
    final var body =
        Map.of(
            "serviceName",
            "REVOKE_CERTIFICATE",
            "resultCode",
            "ERROR",
            "errorId",
            "VALIDATION_ERROR");
    final var ruleResponse = restTemplate.postForEntity(BEHAVIOR_PATH, body, Map.class);
    assertEquals(HttpStatus.CREATED, ruleResponse.getStatusCode());

    final var resource = new ClassPathResource("soap/revoke-certificate.xml");
    final var soapBody = resource.getContentAsString(StandardCharsets.UTF_8);
    final var headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");
    final var soapResponse =
        restTemplate
            .exchange(
                REVOKE_SOAP_PATH,
                HttpMethod.POST,
                new HttpEntity<>(soapBody, headers),
                String.class)
            .getBody();

    assertTrue(soapResponse.contains("ERROR"), "Expected ERROR in SOAP response");
    final var count =
        restTemplate.getForEntity(REVOKE_CERT_PATH + "/count", CountResponse.class).getBody();
    assertEquals(0, count.count());
  }

  @Test
  void errorRuleForSendMessageToRecipientShouldReturnErrorAndNotStoreMessage() throws IOException {
    final var body =
        Map.of(
            "serviceName",
            "SEND_MESSAGE_TO_RECIPIENT",
            "resultCode",
            "ERROR",
            "errorId",
            "VALIDATION_ERROR");
    final var ruleResponse = restTemplate.postForEntity(BEHAVIOR_PATH, body, Map.class);
    assertEquals(HttpStatus.CREATED, ruleResponse.getStatusCode());

    final var resource = new ClassPathResource("soap/send-message-to-recipient.xml");
    final var soapBody = resource.getContentAsString(StandardCharsets.UTF_8);
    final var headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");
    final var soapResponse =
        restTemplate
            .exchange(
                SEND_MESSAGE_SOAP_PATH,
                HttpMethod.POST,
                new HttpEntity<>(soapBody, headers),
                String.class)
            .getBody();

    assertTrue(soapResponse.contains("ERROR"), "Expected ERROR in SOAP response");
    final var count =
        restTemplate.getForEntity(SEND_MESSAGE_PATH + "/count", CountResponse.class).getBody();
    assertEquals(0, count.count());
  }

  @Test
  void errorRuleForCertificateStatusUpdateForCareShouldReturnErrorAndNotStoreUpdate()
      throws IOException {
    final var body =
        Map.of(
            "serviceName",
            "CERTIFICATE_STATUS_UPDATE_FOR_CARE",
            "resultCode",
            "ERROR",
            "errorId",
            "VALIDATION_ERROR");
    final var ruleResponse = restTemplate.postForEntity(BEHAVIOR_PATH, body, Map.class);
    assertEquals(HttpStatus.CREATED, ruleResponse.getStatusCode());

    final var resource = new ClassPathResource("soap/certificate-status-update-for-care.xml");
    final var soapBody = resource.getContentAsString(StandardCharsets.UTF_8);
    final var headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");
    final var soapResponse =
        restTemplate
            .exchange(
                CERTIFICATE_STATUS_SOAP_PATH,
                HttpMethod.POST,
                new HttpEntity<>(soapBody, headers),
                String.class)
            .getBody();

    assertTrue(soapResponse.contains("ERROR"), "Expected ERROR in SOAP response");
    final var count =
        restTemplate
            .getForEntity(CERTIFICATE_STATUS_PATH + "/count", CountResponse.class)
            .getBody();
    assertEquals(0, count.count());
  }

  private void createErrorRule(String certificateId) {
    final var body =
        certificateId != null
            ? Map.of(
                "serviceName",
                "REGISTER_CERTIFICATE",
                "resultCode",
                "ERROR",
                "errorId",
                "VALIDATION_ERROR",
                "matchCriteria",
                Map.of("certificateId", certificateId))
            : Map.of(
                "serviceName",
                "REGISTER_CERTIFICATE",
                "resultCode",
                "ERROR",
                "errorId",
                "VALIDATION_ERROR");
    final var response = restTemplate.postForEntity(BEHAVIOR_PATH, body, Map.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  private void createErrorRuleWithMaxTriggerCount(int maxTriggerCount) {
    final var body =
        Map.of(
            "serviceName",
            "REGISTER_CERTIFICATE",
            "resultCode",
            "ERROR",
            "errorId",
            "VALIDATION_ERROR",
            "maxTriggerCount",
            maxTriggerCount);
    restTemplate.postForEntity(BEHAVIOR_PATH, body, Map.class);
  }

  private void createDelayOnlyRule(long delayMillis) {
    final var body = Map.of("serviceName", "REGISTER_CERTIFICATE", "delayMillis", delayMillis);
    restTemplate.postForEntity(BEHAVIOR_PATH, body, Map.class);
  }

  private String postSoap(String resourcePath) throws IOException {
    final var resource = new ClassPathResource(resourcePath);
    final var body = resource.getContentAsString(StandardCharsets.UTF_8);

    final var headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);
    headers.set("SOAPAction", "\"\"");

    return restTemplate
        .exchange(SOAP_PATH, HttpMethod.POST, new HttpEntity<>(body, headers), String.class)
        .getBody();
  }
}
