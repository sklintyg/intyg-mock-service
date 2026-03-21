package se.inera.intyg.intygmockservice.application.behavior.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BehaviorRuleDTO {
  UUID id;
  String serviceName;
  String resultCode;
  String errorId;
  String resultText;
  Long delayMillis;
  MatchCriteriaDTO matchCriteria;
  Integer maxTriggerCount;
  int triggerCount;
  Instant createdAt;

  @Value
  @Builder
  public static class MatchCriteriaDTO {
    String logicalAddress;
    String certificateId;
    String personId;
  }
}
