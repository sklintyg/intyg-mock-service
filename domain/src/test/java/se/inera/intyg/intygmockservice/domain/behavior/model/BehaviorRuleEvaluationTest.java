package se.inera.intyg.intygmockservice.domain.behavior.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.domain.behavior.service.BehaviorEventLogger;
import se.inera.intyg.intygmockservice.domain.behavior.service.DelayApplier;

@ExtendWith(MockitoExtension.class)
class BehaviorRuleEvaluationTest {

  @Mock private DelayApplier delayApplier;
  @Mock private BehaviorEventLogger eventLogger;
  @Mock private Runnable onExhausted;

  @Mock private DelayApplier secondDelayApplier;
  @Mock private BehaviorEventLogger secondEventLogger;
  @Mock private Runnable secondOnExhausted;

  private final MatchContext context =
      MatchContext.builder().logicalAddress("addr").certificateId("cert-1").build();

  private BehaviorRule.BehaviorRuleBuilder baseRule() {
    return BehaviorRule.builder()
        .id(UUID.randomUUID())
        .serviceName(ServiceName.REGISTER_CERTIFICATE)
        .triggerCount(0)
        .createdAt(Instant.now());
  }

  @Test
  void evaluatingUnwiredRuleWithDelayRejectsWithClearMessage() {
    final var rule = baseRule().delayMillis(100L).build();

    final var exception = assertThrows(IllegalStateException.class, () -> rule.evaluate(context));
    assertTrue(exception.getMessage().contains("wired before evaluation"));
  }

  @Test
  void evaluatingUnwiredRuleWithErrorEffectRejectsWithClearMessage() {
    final var rule = baseRule().resultCode("ERROR").build();

    final var exception = assertThrows(IllegalStateException.class, () -> rule.evaluate(context));
    assertTrue(exception.getMessage().contains("wired before evaluation"));
  }

  @Test
  void buildingRuleWithBothResultCodeAndErrorIdSucceeds() {
    final var rule = baseRule().resultCode("ERROR").errorId("VALIDATION_ERROR").build();
    assertTrue(rule.hasErrorEffect());
  }

  @Test
  void ruleWithDelayAndErrorAppliesBothEffects() {
    final var rule = baseRule().delayMillis(50L).resultCode("ERROR").build();
    rule.wire(delayApplier, eventLogger, onExhausted);

    final var result = rule.evaluate(context);

    verify(delayApplier).apply(50L);
    assertTrue(result.isPresent());
  }

  @Test
  void ruleWithNoEffectsStillIncrementsTriggerCount() {
    final var rule = baseRule().build();
    rule.wire(delayApplier, eventLogger, onExhausted);

    final var result = rule.evaluate(context);

    assertEquals(1, rule.getTriggerCount());
    assertFalse(result.isPresent());
    verify(delayApplier, never()).apply(50L);
  }

  @Test
  void repeatedTriggersIncrementCountCorrectly() {
    final var rule = baseRule().build();

    for (int i = 0; i < 5; i++) {
      rule.trigger();
    }

    assertEquals(5, rule.getTriggerCount());
    assertFalse(rule.isExhausted());
  }

  @Test
  void triggeringPastMaximumKeepsRuleExhausted() {
    final var rule = baseRule().maxTriggerCount(2).build();

    rule.trigger();
    rule.trigger();
    rule.trigger();

    assertTrue(rule.isExhausted());
    assertEquals(3, rule.getTriggerCount());
  }

  @Test
  void exhaustionCallbackFiresOnEveryEvaluationAfterExhaustion() {
    final var rule = baseRule().resultCode("ERROR").maxTriggerCount(1).build();
    rule.wire(delayApplier, eventLogger, onExhausted);

    rule.evaluate(context);
    rule.evaluate(context);

    verify(onExhausted, times(2)).run();
  }

  @Test
  void rewiringOverwritesPreviousDependencies() {
    final var rule = baseRule().delayMillis(50L).build();
    rule.wire(delayApplier, eventLogger, onExhausted);
    rule.wire(secondDelayApplier, secondEventLogger, secondOnExhausted);

    rule.evaluate(context);

    verify(secondDelayApplier).apply(50L);
    verify(delayApplier, never()).apply(50L);
  }
}
