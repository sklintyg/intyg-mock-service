package se.inera.intyg.intygmockservice.behavior;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

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

  public boolean delete(UUID id) {
    return rules.remove(id) != null;
  }

  public void deleteAll() {
    rules.clear();
  }

  public void deleteByServiceName(ServiceName serviceName) {
    rules.entrySet().removeIf(e -> e.getValue().getServiceName() == serviceName);
  }

  public void incrementTriggerCount(UUID id) {
    rules.compute(
        id,
        (k, existing) -> {
          if (existing == null) {
            return null;
          }
          final var updated =
              existing.toBuilder().triggerCount(existing.getTriggerCount() + 1).build();
          if (updated.getMaxTriggerCount() != null
              && updated.getTriggerCount() >= updated.getMaxTriggerCount()) {
            return null;
          }
          return updated;
        });
  }
}
