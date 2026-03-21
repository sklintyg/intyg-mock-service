package se.inera.intyg.intygmockservice.application.behavior.dto;

import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;

public record CreateBehaviorRuleRequest(
    ServiceName serviceName,
    String resultCode,
    String errorId,
    String resultText,
    Long delayMillis,
    MatchCriteriaRequest matchCriteria,
    Integer maxTriggerCount) {

  public record MatchCriteriaRequest(
      String logicalAddress, String certificateId, String personId) {}
}
