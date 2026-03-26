package se.inera.intyg.intygmockservice.domain.behavior.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BehaviorRuleEvaluationTest {

  @Mock private RuleEffectHandler effectHandler;
  @Mock private RuleEffectHandler secondEffectHandler;

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
  void delayOnlyRulePassesDelayRequestedAndNoErrorResult() {
    final var rule = baseRule().delayMillis(100L).build();
    rule.wire(effectHandler);

    rule.evaluate(context);

    final var captor = forClass(RuleEvaluation.class);
    verify(effectHandler).handle(captor.capture());
    final var evaluation = captor.getValue();
    assertTrue(evaluation.delayRequested());
    assertEquals(100L, evaluation.delayMillis());
    assertTrue(evaluation.errorResult().isEmpty());
  }

  @Test
  void errorOnlyRulePassesErrorResultAndNoDelay() {
    final var rule = baseRule().resultCode("ERROR").errorId("VALIDATION_ERROR").build();
    rule.wire(effectHandler);

    rule.evaluate(context);

    final var captor = forClass(RuleEvaluation.class);
    verify(effectHandler).handle(captor.capture());
    final var evaluation = captor.getValue();
    assertFalse(evaluation.delayRequested());
    assertTrue(evaluation.errorResult().isPresent());
    assertEquals("ERROR", evaluation.errorResult().get().getResultCode());
  }

  @Test
  void combinedRulePassesBothDelayAndError() {
    final var rule = baseRule().delayMillis(50L).resultCode("ERROR").build();
    rule.wire(effectHandler);

    rule.evaluate(context);

    final var captor = forClass(RuleEvaluation.class);
    verify(effectHandler).handle(captor.capture());
    final var evaluation = captor.getValue();
    assertTrue(evaluation.delayRequested());
    assertTrue(evaluation.errorResult().isPresent());
  }

  @Test
  void noEffectRulePassesNoDelayNoErrorNotExhausted() {
    final var rule = baseRule().build();
    rule.wire(effectHandler);

    rule.evaluate(context);

    final var captor = forClass(RuleEvaluation.class);
    verify(effectHandler).handle(captor.capture());
    final var evaluation = captor.getValue();
    assertFalse(evaluation.delayRequested());
    assertTrue(evaluation.errorResult().isEmpty());
    assertFalse(evaluation.exhausted());
  }

  @Test
  void exhaustedFlagSetWhenTriggerCountReachesMax() {
    final var rule = baseRule().resultCode("ERROR").maxTriggerCount(1).build();
    rule.wire(effectHandler);

    rule.evaluate(context);

    final var captor = forClass(RuleEvaluation.class);
    verify(effectHandler).handle(captor.capture());
    assertTrue(captor.getValue().exhausted());
  }

  @Test
  void exhaustedFlagNotSetBeforeMaxReached() {
    final var rule = baseRule().resultCode("ERROR").maxTriggerCount(2).build();
    rule.wire(effectHandler);

    rule.evaluate(context);

    final var captor = forClass(RuleEvaluation.class);
    verify(effectHandler).handle(captor.capture());
    assertFalse(captor.getValue().exhausted());
  }

  @Test
  void handlerCalledOnEveryEvaluationAfterExhaustion() {
    final var rule = baseRule().resultCode("ERROR").maxTriggerCount(1).build();
    rule.wire(effectHandler);

    rule.evaluate(context);
    rule.evaluate(context);

    verify(effectHandler, times(2)).handle(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void rewiringOverwritesPreviousHandler() {
    final var rule = baseRule().delayMillis(50L).build();
    rule.wire(effectHandler);
    rule.wire(secondEffectHandler);

    rule.evaluate(context);

    verify(secondEffectHandler).handle(org.mockito.ArgumentMatchers.any());
    verify(effectHandler, org.mockito.Mockito.never()).handle(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void evaluationContainsCorrectServiceNameAndCertificateId() {
    final var rule = baseRule().build();
    rule.wire(effectHandler);

    rule.evaluate(context);

    final var captor = forClass(RuleEvaluation.class);
    verify(effectHandler).handle(captor.capture());
    assertEquals(ServiceName.REGISTER_CERTIFICATE, captor.getValue().serviceName());
    assertEquals("cert-1", captor.getValue().certificateId());
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
  void buildingRuleWithBothResultCodeAndErrorIdSucceeds() {
    final var rule = baseRule().resultCode("ERROR").errorId("VALIDATION_ERROR").build();
    assertTrue(rule.hasErrorEffect());
  }
}
