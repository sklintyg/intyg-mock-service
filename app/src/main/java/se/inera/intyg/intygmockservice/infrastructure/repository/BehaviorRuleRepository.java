package se.inera.intyg.intygmockservice.behavior;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.domain.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.MatchContext;
import se.inera.intyg.intygmockservice.domain.ServiceName;

@Repository
public class BehaviorRuleRepository {

  private final ConcurrentHashMap<UUID, BehaviorRule> rules = new ConcurrentHashMap<>();

  public BehaviorRule save(BehaviorRule rule) {
    rules.put(rule.getId(), rule);
    return rule;
  }

  public Optional<BehaviorRule> findById(UUID id) {
    return Optional.ofNullable(rules.get(id));
  }

  public List<BehaviorRule> findAll() {
    return List.copyOf(rules.values());
  }

  public List<BehaviorRule> findByServiceName(ServiceName serviceName) {
    return rules.values().stream().filter(r -> r.getServiceName() == serviceName).toList();
  }

  public Optional<BehaviorRule> findBestMatch(ServiceName serviceName, MatchContext context) {
    return rules.values().stream()
        .filter(rule -> rule.getServiceName() == serviceName)
        .filter(rule -> rule.matches(context))
        .sorted(
            Comparator.comparingInt(BehaviorRule::specificity)
                .reversed()
                .thenComparing(Comparator.comparing(BehaviorRule::getCreatedAt).reversed()))
        .findFirst();
  }

  public Optional<BehaviorRule> triggerAndPersist(UUID id) {
    final var result = new BehaviorRule[1];
    rules.compute(
        id,
        (k, existing) -> {
          if (existing == null) {
            return null;
          }
          result[0] = existing;
          final var exhausted = existing.trigger();
          if (exhausted) {
            return null;
          }
          return existing;
        });
    return Optional.ofNullable(result[0]);
  }

  public boolean delete(UUID id) {
    return rules.remove(id) != null;
  }

  public void deleteAll() {
    rules.clear();
  }

  public void deleteByServiceName(ServiceName serviceName) {
    rules.entrySet().removeIf(e -> e.getValue().getServiceName() == serviceName);
  }
}
