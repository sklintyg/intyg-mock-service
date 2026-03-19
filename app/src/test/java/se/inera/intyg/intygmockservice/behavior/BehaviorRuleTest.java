package se.inera.intyg.intygmockservice.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BehaviorRuleTest {

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
}
