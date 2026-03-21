package se.inera.intyg.intygmockservice.application.behavior.service;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.behavior.model.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;
import se.inera.intyg.intygmockservice.infrastructure.logging.BehaviorLogger;

class BehaviorLoggerTest {

  private final BehaviorLogger logger = new BehaviorLogger();

  private BehaviorRule errorRule() {
    return BehaviorRule.builder()
        .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
        .serviceName(ServiceName.REGISTER_CERTIFICATE)
        .resultCode("ERROR")
        .errorId("VALIDATION_ERROR")
        .triggerCount(0)
        .createdAt(Instant.now())
        .build();
  }

  private BehaviorRule delayRule() {
    return BehaviorRule.builder()
        .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))
        .serviceName(ServiceName.REGISTER_CERTIFICATE)
        .delayMillis(100L)
        .triggerCount(0)
        .createdAt(Instant.now())
        .build();
  }

  @Test
  void shouldLogErrorSkippedWithoutException() {
    logger.logErrorSkipped(ServiceName.REGISTER_CERTIFICATE, "abc-123", errorRule());
  }

  @Test
  void shouldLogDelayAppliedWithoutException() {
    logger.logDelayApplied(ServiceName.REGISTER_CERTIFICATE, "abc-123", delayRule());
  }

  @Test
  void shouldLogErrorSkippedWithNullCertificateId() {
    logger.logErrorSkipped(ServiceName.REGISTER_CERTIFICATE, null, errorRule());
  }
}
