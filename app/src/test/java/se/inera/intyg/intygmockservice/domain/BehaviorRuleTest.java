package se.inera.intyg.intygmockservice.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BehaviorRuleTest {

  @Mock private DelayApplier delayApplier;
  @Mock private BehaviorEventLogger eventLogger;
  @Mock private Runnable onExhausted;

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
  void hasErrorEffectReturnsTrueWhenResultCodeSet() {
    assertTrue(baseRule().resultCode("ERROR").build().hasErrorEffect());
  }

  @Test
  void hasErrorEffectReturnsFalseWhenResultCodeNull() {
    assertFalse(baseRule().build().hasErrorEffect());
  }

  @Test
  void hasDelayReturnsTrueWhenDelayMillisSet() {
    assertTrue(baseRule().delayMillis(100L).build().hasDelay());
  }

  @Test
  void hasDelayReturnsFalseWhenDelayMillisNull() {
    assertFalse(baseRule().build().hasDelay());
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
    rule.wire(delayApplier, eventLogger, onExhausted);

    final var result = rule.evaluate(context);

    assertTrue(result.isEmpty());
  }

  @Test
  void evaluateAppliesDelayWhenRuleHasDelay() {
    final var rule = baseRule().delayMillis(50L).build();
    rule.wire(delayApplier, eventLogger, onExhausted);

    rule.evaluate(context);

    verify(delayApplier).apply(50L);
  }

  @Test
  void evaluateDoesNotApplyDelayWhenNoDelay() {
    final var rule = baseRule().resultCode("ERROR").build();
    rule.wire(delayApplier, eventLogger, onExhausted);

    rule.evaluate(context);

    verify(delayApplier, never()).apply(anyLong());
  }

  @Test
  void evaluateReturnsResultForErrorRule() {
    final var rule =
        baseRule().resultCode("ERROR").errorId("VALIDATION_ERROR").resultText("msg").build();
    rule.wire(delayApplier, eventLogger, onExhausted);

    final var result = rule.evaluate(context);

    assertTrue(result.isPresent());
    assertEquals("ERROR", result.get().getResultCode());
    assertEquals("VALIDATION_ERROR", result.get().getErrorId());
    assertEquals("msg", result.get().getResultText());
  }

  @Test
  void evaluateIncrementsTriggerCount() {
    final var rule = baseRule().build();
    rule.wire(delayApplier, eventLogger, onExhausted);

    rule.evaluate(context);

    assertEquals(1, rule.getTriggerCount());
  }

  @Test
  void evaluateLogsDelayApplied() {
    final var rule = baseRule().delayMillis(100L).build();
    rule.wire(delayApplier, eventLogger, onExhausted);

    rule.evaluate(context);

    verify(eventLogger).logDelayApplied(ServiceName.REGISTER_CERTIFICATE, "cert-1", rule);
  }

  @Test
  void evaluateLogsErrorSkipped() {
    final var rule = baseRule().resultCode("ERROR").build();
    rule.wire(delayApplier, eventLogger, onExhausted);

    rule.evaluate(context);

    verify(eventLogger).logErrorSkipped(ServiceName.REGISTER_CERTIFICATE, "cert-1", rule);
  }

  @Test
  void evaluateFiresOnExhaustedCallbackWhenExhausted() {
    final var rule = baseRule().resultCode("ERROR").maxTriggerCount(1).build();
    rule.wire(delayApplier, eventLogger, onExhausted);

    rule.evaluate(context);

    verify(onExhausted).run();
  }

  @Test
  void evaluateDoesNotFireOnExhaustedCallbackWhenNotExhausted() {
    final var rule = baseRule().resultCode("ERROR").maxTriggerCount(2).build();
    rule.wire(delayApplier, eventLogger, onExhausted);

    rule.evaluate(context);

    verify(onExhausted, never()).run();
  }
}
