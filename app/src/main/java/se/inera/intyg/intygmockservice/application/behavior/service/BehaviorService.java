package se.inera.intyg.intygmockservice.application.behavior.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.behavior.dto.BehaviorRuleDTO;
import se.inera.intyg.intygmockservice.application.behavior.dto.BehaviorRuleDTO.MatchCriteriaDTO;
import se.inera.intyg.intygmockservice.application.behavior.dto.CreateBehaviorRuleRequest;
import se.inera.intyg.intygmockservice.domain.behavior.model.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.behavior.model.MatchCriteria;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;
import se.inera.intyg.intygmockservice.domain.behavior.repository.BehaviorRuleRepository;

@Service
@RequiredArgsConstructor
public class BehaviorService {

  private final BehaviorRuleRepository repository;

  public BehaviorRuleDTO create(CreateBehaviorRuleRequest request) {
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
    return toDto(repository.save(rule));
  }

  public List<BehaviorRuleDTO> findAll() {
    return repository.findAll().stream().map(this::toDto).toList();
  }

  public List<BehaviorRuleDTO> findByServiceName(String serviceName) {
    return repository.findByServiceName(ServiceName.valueOf(serviceName)).stream()
        .map(this::toDto)
        .toList();
  }

  public Optional<BehaviorRuleDTO> findById(UUID id) {
    return repository.findById(id).map(this::toDto);
  }

  public boolean delete(UUID id) {
    return repository.delete(id);
  }

  public void deleteAll() {
    repository.deleteAll();
  }

  public void deleteByServiceName(String serviceName) {
    repository.deleteByServiceName(ServiceName.valueOf(serviceName));
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

  private BehaviorRuleDTO toDto(BehaviorRule rule) {
    return BehaviorRuleDTO.builder()
        .id(rule.getId())
        .serviceName(rule.getServiceName() != null ? rule.getServiceName().name() : null)
        .resultCode(rule.getResultCode())
        .errorId(rule.getErrorId())
        .resultText(rule.getResultText())
        .delayMillis(rule.getDelayMillis())
        .matchCriteria(
            rule.getMatchCriteria() != null
                ? MatchCriteriaDTO.builder()
                    .logicalAddress(rule.getMatchCriteria().getLogicalAddress())
                    .certificateId(rule.getMatchCriteria().getCertificateId())
                    .personId(rule.getMatchCriteria().getPersonId())
                    .build()
                : null)
        .maxTriggerCount(rule.getMaxTriggerCount())
        .triggerCount(rule.getTriggerCount())
        .createdAt(rule.getCreatedAt())
        .build();
  }
}
