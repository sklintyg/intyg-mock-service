package se.inera.intyg.intygmockservice.behavior;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = BehaviorRule.BehaviorRuleBuilder.class)
public class BehaviorRule {
  UUID id;
  ServiceName serviceName;
  String resultCode;
  String errorId;
  String resultText;
  Long delayMillis;
  MatchCriteria matchCriteria;
  Integer maxTriggerCount;
  int triggerCount;
  Instant createdAt;

  @Value
  @Builder
  @JsonDeserialize(builder = MatchCriteria.MatchCriteriaBuilder.class)
  public static class MatchCriteria {
    String logicalAddress;
    String certificateId;
    String personId;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class MatchCriteriaBuilder {}
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class BehaviorRuleBuilder {}
}
