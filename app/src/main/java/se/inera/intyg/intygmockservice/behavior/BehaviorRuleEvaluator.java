package se.inera.intyg.intygmockservice.behavior;

import java.util.Comparator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.behavior.delay.DelayApplier;

@Component
@RequiredArgsConstructor
public class BehaviorRuleEvaluator {

  private final BehaviorRuleRepository repository;
  private final DelayApplier delayApplier;

  public Optional<BehaviorRule> evaluate(ServiceName serviceName, MatchContext context) {
    final var matching =
        repository.findByServiceName(serviceName).stream()
            .filter(rule -> matches(rule, context))
            .sorted(
                Comparator.comparingInt(this::specificity)
                    .reversed()
                    .thenComparing(Comparator.comparing(BehaviorRule::getCreatedAt).reversed()))
            .findFirst();

    matching.ifPresent(
        rule -> {
          if (rule.getDelayMillis() != null) {
            delayApplier.apply(rule.getDelayMillis());
          }
          repository.incrementTriggerCount(rule.getId());
        });

    return matching;
  }

  private boolean matches(BehaviorRule rule, MatchContext context) {
    if (rule.getMatchCriteria() == null) {
      return true;
    }
    final var criteria = rule.getMatchCriteria();
    if (criteria.getLogicalAddress() != null
        && !criteria.getLogicalAddress().equals(context.getLogicalAddress())) {
      return false;
    }
    if (criteria.getCertificateId() != null
        && !criteria.getCertificateId().equals(context.getCertificateId())) {
      return false;
    }
    if (criteria.getPersonId() != null
        && !matchesPersonId(criteria.getPersonId(), context.getPersonId())) {
      return false;
    }
    return true;
  }

  private boolean matchesPersonId(String ruleCriteria, String contextPersonId) {
    if (contextPersonId == null) {
      return false;
    }
    return ruleCriteria.replace("-", "").equals(contextPersonId.replace("-", ""));
  }

  private int specificity(BehaviorRule rule) {
    if (rule.getMatchCriteria() == null) {
      return 0;
    }
    int count = 0;
    if (rule.getMatchCriteria().getLogicalAddress() != null) {
      count++;
    }
    if (rule.getMatchCriteria().getCertificateId() != null) {
      count++;
    }
    if (rule.getMatchCriteria().getPersonId() != null) {
      count++;
    }
    return count;
  }
}
