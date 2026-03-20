package se.inera.intyg.intygmockservice.application.common.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.application.common.behavior.service.BehaviorService;
import se.inera.intyg.intygmockservice.application.common.behavior.service.CreateBehaviorRuleRequest;
import se.inera.intyg.intygmockservice.domain.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.DelayApplier;
import se.inera.intyg.intygmockservice.domain.MatchContext;
import se.inera.intyg.intygmockservice.domain.ServiceName;
import se.inera.intyg.intygmockservice.infrastructure.repository.BehaviorRuleRepository;

@ExtendWith(MockitoExtension.class)
class BehaviorServiceTest {

  @Mock private BehaviorRuleRepository repository;
  @Mock private DelayApplier delayApplier;

  @InjectMocks private BehaviorService behaviorService;

  private final MatchContext context =
      MatchContext.builder().logicalAddress("addr").certificateId("cert").build();

  @Test
  void evaluateReturnsMatchingRule() {
    final var rule = rule(null, null);
    when(repository.findBestMatch(ServiceName.REGISTER_CERTIFICATE, context))
        .thenReturn(Optional.of(rule));
    when(repository.triggerAndPersist(rule.getId())).thenReturn(Optional.of(rule));

    final var result = behaviorService.evaluate(ServiceName.REGISTER_CERTIFICATE, context);

    assertTrue(result.isPresent());
    assertEquals(rule.getId(), result.get().getId());
  }

  @Test
  void evaluateReturnsEmptyWhenNoMatch() {
    when(repository.findBestMatch(any(), any())).thenReturn(Optional.empty());

    final var result = behaviorService.evaluate(ServiceName.REGISTER_CERTIFICATE, context);

    assertTrue(result.isEmpty());
  }

  @Test
  void evaluateAppliesDelayWhenRuleHasDelay() {
    final var rule = rule(50L, null);
    when(repository.findBestMatch(any(), any())).thenReturn(Optional.of(rule));
    when(repository.triggerAndPersist(rule.getId())).thenReturn(Optional.of(rule));

    behaviorService.evaluate(ServiceName.REGISTER_CERTIFICATE, context);

    verify(delayApplier).apply(50L);
  }

  @Test
  void evaluateDoesNotApplyDelayWhenNoDelay() {
    final var rule = rule(null, null);
    when(repository.findBestMatch(any(), any())).thenReturn(Optional.of(rule));
    when(repository.triggerAndPersist(rule.getId())).thenReturn(Optional.of(rule));

    behaviorService.evaluate(ServiceName.REGISTER_CERTIFICATE, context);

    verify(delayApplier, never()).apply(any(long.class));
  }

  @Test
  void evaluateTriggersRule() {
    final var rule = rule(null, null);
    when(repository.findBestMatch(any(), any())).thenReturn(Optional.of(rule));
    when(repository.triggerAndPersist(rule.getId())).thenReturn(Optional.of(rule));

    behaviorService.evaluate(ServiceName.REGISTER_CERTIFICATE, context);

    verify(repository).triggerAndPersist(rule.getId());
  }

  @Test
  void createBuildsAndSavesRule() {
    final var request =
        new CreateBehaviorRuleRequest(
            ServiceName.REGISTER_CERTIFICATE,
            "ERROR",
            "VALIDATION_ERROR",
            "text",
            100L,
            new CreateBehaviorRuleRequest.MatchCriteriaRequest("addr", "cert", "person"),
            5);
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    final var result = behaviorService.create(request);

    assertNotNull(result.getId());
    assertEquals("REGISTER_CERTIFICATE", result.getServiceName());
    assertEquals("ERROR", result.getResultCode());
    assertEquals("addr", result.getMatchCriteria().getLogicalAddress());
    assertEquals(0, result.getTriggerCount());
    verify(repository).save(any());
  }

  @Test
  void createHandlesNullMatchCriteria() {
    final var request =
        new CreateBehaviorRuleRequest(
            ServiceName.REGISTER_CERTIFICATE, "ERROR", null, null, null, null, null);
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    final var result = behaviorService.create(request);

    assertNotNull(result);
    assertEquals(null, result.getMatchCriteria());
  }

  @Test
  void deleteAllDelegatesToRepository() {
    behaviorService.deleteAll();

    verify(repository).deleteAll();
  }

  @Test
  void deleteByServiceNameDelegatesToRepository() {
    behaviorService.deleteByServiceName("REGISTER_CERTIFICATE");

    verify(repository).deleteByServiceName(ServiceName.REGISTER_CERTIFICATE);
  }

  @Test
  void deleteDelegatesToRepository() {
    final var id = UUID.randomUUID();

    behaviorService.delete(id);

    verify(repository).delete(id);
  }

  @Test
  void findByIdDelegatesToRepository() {
    final var id = UUID.randomUUID();
    behaviorService.findById(id);

    verify(repository).findById(id);
  }

  @Test
  void findAllDelegatesToRepository() {
    behaviorService.findAll();

    verify(repository).findAll();
  }

  @Test
  void findByServiceNameDelegatesToRepository() {
    behaviorService.findByServiceName("REGISTER_CERTIFICATE");

    verify(repository).findByServiceName(eq(ServiceName.REGISTER_CERTIFICATE));
  }

  private BehaviorRule rule(Long delayMillis, Integer maxTriggerCount) {
    return BehaviorRule.builder()
        .id(UUID.randomUUID())
        .serviceName(ServiceName.REGISTER_CERTIFICATE)
        .resultCode("ERROR")
        .delayMillis(delayMillis)
        .maxTriggerCount(maxTriggerCount)
        .triggerCount(0)
        .createdAt(Instant.now())
        .build();
  }
}
