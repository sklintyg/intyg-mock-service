package se.inera.intyg.intygmockservice.behavior;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

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

  @JsonPOJOBuilder(withPrefix = "")
  public static final class BehaviorRuleBuilder {}
}
