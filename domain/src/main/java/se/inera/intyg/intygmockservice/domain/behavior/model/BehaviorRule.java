package se.inera.intyg.intygmockservice.domain.behavior.model;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class BehaviorRule {

  public static class BehaviorRuleBuilder {
    public BehaviorRule build() {
      if (errorId != null && resultCode == null) {
        throw new IllegalArgumentException("BehaviorRule requires resultCode when errorId is set");
      }
      return new BehaviorRule(
          id,
          serviceName,
          resultCode,
          errorId,
          resultText,
          delayMillis,
          matchCriteria,
          maxTriggerCount,
          triggerCount,
          createdAt,
          effectHandler);
    }
  }

  private final UUID id;
  private final ServiceName serviceName;
  private final String resultCode;
  private final String errorId;
  private final String resultText;
  private final Long delayMillis;
  private final MatchCriteria matchCriteria;
  private final Integer maxTriggerCount;
  private int triggerCount;
  private final Instant createdAt;

  private RuleEffectHandler effectHandler;

  public void wire(RuleEffectHandler handler) {
    this.effectHandler = handler;
  }

  public boolean matches(MatchContext context) {
    if (matchCriteria == null) {
      return true;
    }
    return matchCriteria.matches(context);
  }

  public int specificity() {
    if (matchCriteria == null) {
      return 0;
    }
    return matchCriteria.specificity();
  }

  public boolean returnsError() {
    return resultCode != null;
  }

  public boolean appliesDelay() {
    return delayMillis != null;
  }

  public boolean trigger() {
    triggerCount++;
    return isExhausted();
  }

  public boolean isExhausted() {
    return maxTriggerCount != null && triggerCount >= maxTriggerCount;
  }

  public Optional<MockResponse> evaluate(MatchContext context) {
    if (effectHandler == null) {
      throw new IllegalStateException("BehaviorRule must be wired before evaluation");
    }
    final var delayRequested = appliesDelay();
    trigger();
    final var exhausted = isExhausted();
    final var errorResult =
        returnsError()
            ? Optional.of(
                MockResponse.builder()
                    .resultCode(resultCode)
                    .errorId(errorId)
                    .resultText(resultText)
                    .build())
            : Optional.<MockResponse>empty();
    effectHandler.handle(
        new RuleEvaluation(
            id,
            serviceName,
            context.getCertificateId(),
            errorResult,
            delayRequested,
            delayMillis,
            exhausted));
    return errorResult;
  }
}
