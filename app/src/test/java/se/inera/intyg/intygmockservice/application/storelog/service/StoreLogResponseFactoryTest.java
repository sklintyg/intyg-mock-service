package se.inera.intyg.intygmockservice.application.storelog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.behavior.model.MockResponse;
import se.riv.informationsecurity.auditing.log.v2.ResultCodeType;

class StoreLogResponseFactoryTest {

  private final StoreLogResponseFactory factory = new StoreLogResponseFactory();

  @Test
  void shouldSetResultCodeFromMockResponse() {
    final var result = MockResponse.builder().resultCode("VALIDATION_ERROR").build();

    final var response = factory.create(result);

    assertEquals(ResultCodeType.VALIDATION_ERROR, response.getResult().getResultCode());
  }

  @Test
  void shouldNotSetResultTextWhenNull() {
    final var result = MockResponse.builder().resultCode("OK").resultText(null).build();

    final var response = factory.create(result);

    assertNull(response.getResult().getResultText());
  }

  @Test
  void shouldSetResultTextWhenPresent() {
    final var result =
        MockResponse.builder()
            .resultCode("VALIDATION_ERROR")
            .resultText("Something went wrong")
            .build();

    final var response = factory.create(result);

    assertEquals("Something went wrong", response.getResult().getResultText());
  }
}
