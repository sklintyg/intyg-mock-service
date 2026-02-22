package se.inera.intyg.intygmockservice.storelog;

import static org.assertj.core.api.Assertions.assertThat;
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
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO;

@SpringBootTest(classes = IntygMockServiceApplication.class, webEnvironment = RANDOM_PORT)
class StoreLogIT {

    private static final String SOAP_PATH =
        "/services/informationsecurity/auditing/log/StoreLog/v2/rivtabp21";
    private static final String REST_PATH = "/api/store-log";

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void cleanUp() {
        restTemplate.delete(REST_PATH);
    }

    @Test
    void shouldStoreLogViaSoap() throws IOException {
        postSoap("soap/store-log.xml");

        final var response = restTemplate.getForEntity(REST_PATH, LogTypeDTO[].class);
        final var items = response.getBody();

        assertThat(items).hasSize(1);
        assertThat(items[0].getLogId()).isEqualTo("it-log-001");
    }

    @Test
    void shouldReturnEmptyListWhenNoLogStored() {
        final var response = restTemplate.getForEntity(REST_PATH, LogTypeDTO[].class);

        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldDeleteAllLogs() throws IOException {
        postSoap("soap/store-log.xml");

        restTemplate.delete(REST_PATH);

        final var response = restTemplate.getForEntity(REST_PATH, LogTypeDTO[].class);
        assertThat(response.getBody()).isEmpty();
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
