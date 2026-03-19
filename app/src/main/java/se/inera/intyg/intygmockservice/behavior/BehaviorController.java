package se.inera.intyg.intygmockservice.behavior;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Behavior")
@RestController
@RequestMapping("/api/behavior")
@RequiredArgsConstructor
public class BehaviorController {

  private final BehaviorRuleRepository repository;

  @Operation(summary = "Create a behavior rule")
  @PostMapping
  public ResponseEntity<BehaviorRule> create(@RequestBody CreateBehaviorRuleRequest request) {
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
    return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(rule));
  }

  @Operation(summary = "List behavior rules, optionally filtered by service")
  @GetMapping
  public List<BehaviorRule> list(@RequestParam(required = false) ServiceName service) {
    if (service != null) {
      return repository.findByServiceName(service);
    }
    return repository.findAll();
  }

  @Operation(summary = "Get a specific behavior rule by ID")
  @GetMapping("/{ruleId}")
  public ResponseEntity<BehaviorRule> getById(@PathVariable UUID ruleId) {
    return repository
        .findById(ruleId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @Operation(summary = "Delete a specific behavior rule by ID")
  @DeleteMapping("/{ruleId}")
  public ResponseEntity<Void> deleteById(@PathVariable UUID ruleId) {
    repository.delete(ruleId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Delete behavior rules, optionally filtered by service")
  @DeleteMapping
  public ResponseEntity<Void> deleteAll(@RequestParam(required = false) ServiceName service) {
    if (service != null) {
      repository.deleteByServiceName(service);
    } else {
      repository.deleteAll();
    }
    return ResponseEntity.noContent().build();
  }

  private BehaviorRule.MatchCriteria toMatchCriteria(
      CreateBehaviorRuleRequest.MatchCriteriaRequest request) {
    if (request == null) {
      return null;
    }
    return BehaviorRule.MatchCriteria.builder()
        .logicalAddress(request.logicalAddress())
        .certificateId(request.certificateId())
        .personId(request.personId())
        .build();
  }
}
