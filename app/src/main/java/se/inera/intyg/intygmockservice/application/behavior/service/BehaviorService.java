package se.inera.intyg.intygmockservice.application.behavior.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.behavior.dto.CreateBehaviorRuleRequest;
import se.inera.intyg.intygmockservice.domain.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.DelayApplier;
import se.inera.intyg.intygmockservice.domain.MatchContext;
import se.inera.intyg.intygmockservice.domain.MatchCriteria;
import se.inera.intyg.intygmockservice.domain.ServiceName;
import se.inera.intyg.intygmockservice.infrastructure.repository.BehaviorRuleRepository;

@Service
@RequiredArgsConstructor
public class BehaviorService {

  private final BehaviorRuleRepository repository;
  private final DelayApplier delayApplier;

  public Optional<BehaviorRule> evaluate(ServiceName serviceName, MatchContext context) {
    final var ruleOpt = repository.findBestMatch(serviceName, context);

    ruleOpt.ifPresent(
        rule -> {
          if (rule.hasDelay()) {
            delayApplier.apply(rule.getDelayMillis());
          }
          repository.triggerAndPersist(rule.getId());
        });

    return ruleOpt;
  }

  public BehaviorRule create(CreateBehaviorRuleRequest request) {
    final var rule =
        BehaviorRule.builder()
            .id(UUID.randomUUID())
            .serviceName(request.serviceName())
            .resultCode(request.resultCode())
            .errorId(request.errorId())
            .resultText(request.resultText())
            .delayMillis(request.delayMillis())
            .matchCriteria(toMatchCriteria(request.matchCriteria()))
            .maxTriggerCount(request.maxTriggerCount())
            .triggerCount(0)
            .createdAt(Instant.now())
            .build();
    return repository.save(rule);
  }

  public List<BehaviorRule> findAll() {
    return repository.findAll();
  }

  public List<BehaviorRule> findByServiceName(ServiceName serviceName) {
    return repository.findByServiceName(serviceName);
  }

  public Optional<BehaviorRule> findById(UUID id) {
    return repository.findById(id);
  }

  public boolean delete(UUID id) {
    return repository.delete(id);
  }

  public void deleteAll() {
    repository.deleteAll();
  }

  public void deleteByServiceName(ServiceName serviceName) {
    repository.deleteByServiceName(serviceName);
  }

  private MatchCriteria toMatchCriteria(CreateBehaviorRuleRequest.MatchCriteriaRequest request) {
    if (request == null) {
      return null;
    }
    return MatchCriteria.builder()
        .logicalAddress(request.logicalAddress())
        .certificateId(request.certificateId())
        .personId(request.personId())
        .build();
  }
}
