package se.inera.intyg.intygmockservice.sendmessagetorecipient;

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
import se.inera.intyg.intygmockservice.IntygMockServiceApplication;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.dto.SendMessageToRecipientDTO;

@SpringBootTest(classes = IntygMockServiceApplication.class, webEnvironment = RANDOM_PORT)
class SendMessageToRecipientIT {

  private static final String SOAP_PATH =
      "/services/clinicalprocess/healthcond/certificate/SendMessageToRecipient/2/rivtabp21";
  private static final String REST_PATH = "/api/send-message-to-recipient";

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void cleanUp() {
    restTemplate.delete(REST_PATH);
  }

  @Test
  void shouldStoreMessageViaSoap() throws IOException {
    postSoap("soap/send-message-to-recipient.xml");

    final var response = restTemplate.getForEntity(REST_PATH, SendMessageToRecipientDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("it-message-001", items[0].getMeddelandeId());
  }

  @Test
  void shouldReturnEmptyListWhenNoMessageSent() {
    final var response = restTemplate.getForEntity(REST_PATH, SendMessageToRecipientDTO[].class);

    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldDeleteAllMessages() throws IOException {
    postSoap("soap/send-message-to-recipient.xml");

    restTemplate.delete(REST_PATH);

    final var response = restTemplate.getForEntity(REST_PATH, SendMessageToRecipientDTO[].class);
    assertEquals(0, response.getBody().length);
  }

  @Test
  void shouldReturnMessagesByRecipientId() throws IOException {
    postSoap("soap/send-message-to-recipient.xml");

    final var response =
        restTemplate.getForEntity(REST_PATH + "/recipient/FK", SendMessageToRecipientDTO[].class);
    final var items = response.getBody();

    assertEquals(1, items.length);
    assertEquals("FK", items[0].getLogiskAdressMottagare());
  }

  @Test
  void shouldReturnEmptyListForUnknownRecipientId() throws IOException {
    postSoap("soap/send-message-to-recipient.xml");

    final var response =
        restTemplate.getForEntity(
            REST_PATH + "/recipient/UNKNOWN", SendMessageToRecipientDTO[].class);

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
