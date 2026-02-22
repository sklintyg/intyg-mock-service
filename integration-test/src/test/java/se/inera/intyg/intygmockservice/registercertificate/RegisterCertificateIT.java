package se.inera.intyg.intygmockservice.registercertificate;

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
import se.inera.intyg.intygmockservice.registercertificate.dto.RegisterCertificateDTO;

@SpringBootTest(classes = IntygMockServiceApplication.class, webEnvironment = RANDOM_PORT)
class RegisterCertificateIT {

    private static final String SOAP_PATH =
        "/services/clinicalprocess/healthcond/certificate/RegisterCertificate/3/rivtabp21";
    private static final String REST_PATH = "/api/register-certificate";

    @Autowired
    private TestRestTemplate restTemplate;

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

    private void postSoap(String resourcePath) throws IOException {
        final var resource = new ClassPathResource(resourcePath);
        final var body = resource.getContentAsString(StandardCharsets.UTF_8);

        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("SOAPAction", "\"\"");

        restTemplate.exchange(SOAP_PATH, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
    }
}
