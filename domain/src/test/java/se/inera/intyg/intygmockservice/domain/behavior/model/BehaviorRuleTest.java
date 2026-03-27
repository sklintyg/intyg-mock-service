package se.inera.intyg.intygmockservice.domain.behavior.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BehaviorRuleTest {

  @Mock private RuleEffectHandler effectHandler;

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
  void matchesReturnsTrueWhenNullCriteria() {
    final var rule = baseRule().build();
    final var context = MatchContext.builder().logicalAddress("any").build();

    assertTrue(rule.matches(context));
  }

  @Test
  void matchesDelegatesToMatchCriteria() {
    final var rule =
        baseRule().matchCriteria(MatchCriteria.builder().certificateId("cert-123").build()).build();

    assertTrue(rule.matches(MatchContext.builder().certificateId("cert-123").build()));
    assertFalse(rule.matches(MatchContext.builder().certificateId("cert-456").build()));
  }

  @Test
  void specificityReturnsZeroWhenNullCriteria() {
    assertEquals(0, baseRule().build().specificity());
  }

  @Test
  void specificityDelegatesToMatchCriteria() {
    final var rule =
        baseRule()
            .matchCriteria(MatchCriteria.builder().logicalAddress("a").certificateId("b").build())
            .build();

    assertEquals(2, rule.specificity());
  }

  @Test
  void returnsErrorReturnsTrueWhenResultCodeSet() {
    assertTrue(baseRule().resultCode("ERROR").build().returnsError());
  }

  @Test
  void returnsErrorReturnsFalseWhenResultCodeNull() {
    assertFalse(baseRule().build().returnsError());
  }

  @Test
  void buildingRuleWithErrorIdButNoResultCodeThrowsIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class, () -> baseRule().errorId("VALIDATION_ERROR").build());
  }

  @Test
  void appliesDelayReturnsTrueWhenDelayMillisSet() {
    assertTrue(baseRule().delayMillis(100L).build().appliesDelay());
  }

  @Test
  void appliesDelayReturnsFalseWhenDelayMillisNull() {
    assertFalse(baseRule().build().appliesDelay());
  }

  @Test
  void triggerIncrementsTriggerCount() {
    final var rule = baseRule().build();

    rule.trigger();

    assertEquals(1, rule.getTriggerCount());
  }

  @Test
  void triggerReturnsTrueWhenExhausted() {
    final var rule = baseRule().maxTriggerCount(1).build();

    assertTrue(rule.trigger());
  }

  @Test
  void triggerReturnsFalseWhenNotExhausted() {
    final var rule = baseRule().maxTriggerCount(2).build();

    assertFalse(rule.trigger());
  }

  @Test
  void triggerReturnsFalseWhenNoMaxTriggerCount() {
    final var rule = baseRule().build();

    assertFalse(rule.trigger());
  }

  @Test
  void isExhaustedReturnsTrueWhenCountReachesMax() {
    final var rule = baseRule().maxTriggerCount(1).triggerCount(1).build();

    assertTrue(rule.isExhausted());
  }

  @Test
  void isExhaustedReturnsFalseWhenNoMax() {
    final var rule = baseRule().triggerCount(100).build();

    assertFalse(rule.isExhausted());
  }

  @Test
  void evaluateReturnsEmptyForDelayOnlyRule() {
    final var rule = baseRule().delayMillis(50L).build();
    rule.wire(effectHandler);

    assertTrue(rule.evaluate(context).isEmpty());
  }

  @Test
  void evaluateReturnsResultForErrorRule() {
    final var rule =
        baseRule().resultCode("ERROR").errorId("VALIDATION_ERROR").resultText("msg").build();
    rule.wire(effectHandler);

    final var result = rule.evaluate(context);

    assertTrue(result.isPresent());
    assertEquals("ERROR", result.get().getResultCode());
    assertEquals("VALIDATION_ERROR", result.get().getErrorId());
    assertEquals("msg", result.get().getResultText());
  }

  @Test
  void evaluateIncrementsTriggerCount() {
    final var rule = baseRule().build();
    rule.wire(effectHandler);

    rule.evaluate(context);

    assertEquals(1, rule.getTriggerCount());
  }

  @Test
  void evaluateCallsHandlerWithDelayRequestedWhenRuleHasDelay() {
    final var rule = baseRule().delayMillis(50L).build();
    rule.wire(effectHandler);

    rule.evaluate(context);

    verify(effectHandler).handle(argThat(e -> e.delayRequested() && e.delayMillis() == 50L));
  }

  @Test
  void evaluateCallsHandlerWithExhaustedTrueWhenExhausted() {
    final var rule = baseRule().resultCode("ERROR").maxTriggerCount(1).build();
    rule.wire(effectHandler);

    rule.evaluate(context);

    verify(effectHandler).handle(argThat(RuleEvaluation::exhausted));
  }

  @Test
  void evaluateCallsHandlerWithExhaustedFalseWhenNotExhausted() {
    final var rule = baseRule().resultCode("ERROR").maxTriggerCount(2).build();
    rule.wire(effectHandler);

    rule.evaluate(context);

    verify(effectHandler).handle(argThat(e -> !e.exhausted()));
  }

  @Test
  void evaluateThrowsWhenNotWired() {
    final var rule = baseRule().build();

    final var exception = assertThrows(IllegalStateException.class, () -> rule.evaluate(context));
    assertTrue(exception.getMessage().contains("wired before evaluation"));
  }
}
