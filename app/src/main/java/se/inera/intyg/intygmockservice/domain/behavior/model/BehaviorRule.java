package se.inera.intyg.intygmockservice.domain.behavior.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import se.inera.intyg.intygmockservice.domain.behavior.service.BehaviorEventLogger;
import se.inera.intyg.intygmockservice.domain.behavior.service.DelayApplier;

@Getter
@Builder(toBuilder = true)
@JsonDeserialize(builder = BehaviorRule.BehaviorRuleBuilder.class)
public class BehaviorRule {
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

  private DelayApplier delayApplier;
  private BehaviorEventLogger eventLogger;
  private Runnable onExhausted;

  public void wire(
      DelayApplier delayApplier, BehaviorEventLogger eventLogger, Runnable onExhausted) {
    this.delayApplier = delayApplier;
    this.eventLogger = eventLogger;
    this.onExhausted = onExhausted;
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

  public boolean hasErrorEffect() {
    return resultCode != null;
  }

  public boolean hasDelay() {
    return delayMillis != null;
  }

  public boolean trigger() {
    triggerCount++;
    return isExhausted();
  }

  public boolean isExhausted() {
    return maxTriggerCount != null && triggerCount >= maxTriggerCount;
  }

  public Optional<EvaluationResult> evaluate(MatchContext context) {
    if (hasDelay()) {
      delayApplier.apply(delayMillis);
      eventLogger.logDelayApplied(serviceName, context.getCertificateId(), this);
    }

    trigger();

    if (isExhausted() && onExhausted != null) {
      onExhausted.run();
    }

    if (hasErrorEffect()) {
      eventLogger.logErrorSkipped(serviceName, context.getCertificateId(), this);
      return Optional.of(
          EvaluationResult.builder()
              .resultCode(resultCode)
              .errorId(errorId)
              .resultText(resultText)
              .build());
    }

    return Optional.empty();
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class BehaviorRuleBuilder {}
}
