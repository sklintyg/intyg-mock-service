package se.inera.intyg.intygmockservice.application.statusupdates.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.behavior.model.EvaluationResult;
import se.riv.clinicalprocess.healthcond.certificate.v3.ErrorIdType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;

class CertificateStatusUpdateForCareResponseFactoryTest {

  private final CertificateStatusUpdateForCareResponseFactory factory =
      new CertificateStatusUpdateForCareResponseFactory();

  @Test
  void shouldSetResultCodeFromEvaluationResult() {
    final var result = EvaluationResult.builder().resultCode("ERROR").build();

    final var response = factory.create(result);

    assertEquals(ResultCodeType.ERROR, response.getResult().getResultCode());
  }

  @Test
  void shouldNotSetErrorIdWhenNull() {
    final var result = EvaluationResult.builder().resultCode("OK").errorId(null).build();

    final var response = factory.create(result);

    assertNull(response.getResult().getErrorId());
  }

  @Test
  void shouldSetErrorIdWhenPresent() {
    final var result =
        EvaluationResult.builder().resultCode("ERROR").errorId("VALIDATION_ERROR").build();

    final var response = factory.create(result);

    assertEquals(ErrorIdType.VALIDATION_ERROR, response.getResult().getErrorId());
  }

  @Test
  void shouldNotSetResultTextWhenNull() {
    final var result = EvaluationResult.builder().resultCode("OK").resultText(null).build();

    final var response = factory.create(result);

    assertNull(response.getResult().getResultText());
  }

  @Test
  void shouldSetResultTextWhenPresent() {
    final var result =
        EvaluationResult.builder().resultCode("ERROR").resultText("Something went wrong").build();

    final var response = factory.create(result);

    assertEquals("Something went wrong", response.getResult().getResultText());
  }
}
