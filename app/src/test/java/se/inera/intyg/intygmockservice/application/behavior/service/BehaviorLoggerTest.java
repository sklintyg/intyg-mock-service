package se.inera.intyg.intygmockservice.application.behavior.service;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.behavior.model.MockResponse;
import se.inera.intyg.intygmockservice.domain.behavior.model.RuleEvaluation;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;
import se.inera.intyg.intygmockservice.infrastructure.logging.BehaviorLogger;

class BehaviorLoggerTest {

  private final BehaviorLogger logger = new BehaviorLogger();

  private final UUID ruleId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

  private RuleEvaluation errorEvaluation(String certificateId) {
    return new RuleEvaluation(
        ruleId,
        ServiceName.REGISTER_CERTIFICATE,
        certificateId,
        Optional.of(MockResponse.builder().resultCode("ERROR").errorId("VALIDATION_ERROR").build()),
        false,
        null,
        false);
  }

  private RuleEvaluation delayEvaluation() {
    return new RuleEvaluation(
        ruleId, ServiceName.REGISTER_CERTIFICATE, "abc-123", Optional.empty(), true, 100L, false);
  }

  @Test
  void shouldLogErrorSkippedWithoutException() {
    logger.logErrorSkipped(errorEvaluation("abc-123"));
  }

  @Test
  void shouldLogDelayAppliedWithoutException() {
    logger.logDelayApplied(delayEvaluation());
  }

  @Test
  void shouldLogErrorSkippedWithNullCertificateId() {
    logger.logErrorSkipped(errorEvaluation(null));
  }
}
