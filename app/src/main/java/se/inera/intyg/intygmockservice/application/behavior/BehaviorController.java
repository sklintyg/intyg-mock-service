package se.inera.intyg.intygmockservice.application.behavior;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import se.inera.intyg.intygmockservice.application.behavior.dto.CreateBehaviorRuleRequest;
import se.inera.intyg.intygmockservice.application.behavior.service.BehaviorService;
import se.inera.intyg.intygmockservice.domain.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.ServiceName;

@Tag(name = "Behavior")
@RestController
@RequestMapping("/api/behavior")
@RequiredArgsConstructor
public class BehaviorController {

  private final BehaviorService behaviorService;

  @Operation(summary = "Create a behavior rule")
  @PostMapping
  public ResponseEntity<BehaviorRule> create(@RequestBody CreateBehaviorRuleRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(behaviorService.create(request));
  }

  @Operation(summary = "List behavior rules, optionally filtered by service")
  @GetMapping
  public List<BehaviorRule> list(@RequestParam(required = false) ServiceName service) {
    if (service != null) {
      return behaviorService.findByServiceName(service);
    }
    return behaviorService.findAll();
  }

  @Operation(summary = "Get a specific behavior rule by ID")
  @GetMapping("/{ruleId}")
  public ResponseEntity<BehaviorRule> getById(@PathVariable UUID ruleId) {
    return behaviorService
        .findById(ruleId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @Operation(summary = "Delete a specific behavior rule by ID")
  @DeleteMapping("/{ruleId}")
  public ResponseEntity<Void> deleteById(@PathVariable UUID ruleId) {
    behaviorService.delete(ruleId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Delete behavior rules, optionally filtered by service")
  @DeleteMapping
  public ResponseEntity<Void> deleteAll(@RequestParam(required = false) ServiceName service) {
    if (service != null) {
      behaviorService.deleteByServiceName(service);
    } else {
      behaviorService.deleteAll();
    }
    return ResponseEntity.noContent().build();
  }
}
