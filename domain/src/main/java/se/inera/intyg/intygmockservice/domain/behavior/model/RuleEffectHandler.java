package se.inera.intyg.intygmockservice.domain.behavior.model;

@FunctionalInterface
public interface RuleEffectHandler {
  void handle(RuleEvaluation evaluation);
}
