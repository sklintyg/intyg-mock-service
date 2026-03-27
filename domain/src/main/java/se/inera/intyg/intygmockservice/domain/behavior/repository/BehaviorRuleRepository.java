package se.inera.intyg.intygmockservice.domain.behavior.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import se.inera.intyg.intygmockservice.domain.behavior.model.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.behavior.model.MatchContext;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;

public interface BehaviorRuleRepository {

  BehaviorRule save(BehaviorRule rule);

  Optional<BehaviorRule> findById(UUID id);

  List<BehaviorRule> findAll();

  List<BehaviorRule> findByServiceName(ServiceName serviceName);

  Optional<BehaviorRule> findBestMatch(ServiceName serviceName);

  Optional<BehaviorRule> findBestMatch(ServiceName serviceName, MatchContext context);

  boolean delete(UUID id);

  void deleteAll();

  void deleteByServiceName(ServiceName serviceName);
}
