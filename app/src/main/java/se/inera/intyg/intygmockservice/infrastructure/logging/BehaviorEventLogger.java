package se.inera.intyg.intygmockservice.infrastructure.logging;

import se.inera.intyg.intygmockservice.domain.behavior.model.RuleEvaluation;

public interface BehaviorEventLogger {
  void logDelayApplied(RuleEvaluation evaluation);

  void logErrorSkipped(RuleEvaluation evaluation);
}
