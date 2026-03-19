package se.inera.intyg.intygmockservice.behavior;

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
