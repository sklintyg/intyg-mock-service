package se.inera.intyg.intygmockservice.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.behavior.delay.DelayApplier;

@ExtendWith(MockitoExtension.class)
class BehaviorRuleEvaluatorTest {

  @Mock private DelayApplier delayApplier;

  private BehaviorRuleRepository repository;
  private BehaviorRuleEvaluator evaluator;

  @BeforeEach
  void setUp() {
    repository = new BehaviorRuleRepository();
    evaluator = new BehaviorRuleEvaluator(repository, delayApplier);
  }

  private BehaviorRule.MatchCriteria criteria(
      String logicalAddress, String certificateId, String personId) {
    return BehaviorRule.MatchCriteria.builder()
        .logicalAddress(logicalAddress)
        .certificateId(certificateId)
        .personId(personId)
        .build();
  }

  private BehaviorRule rule(BehaviorRule.MatchCriteria matchCriteria) {
    return rule(matchCriteria, Instant.now());
  }

  private BehaviorRule rule(BehaviorRule.MatchCriteria matchCriteria, Instant createdAt) {
    return BehaviorRule.builder()
        .id(UUID.randomUUID())
        .serviceName(ServiceName.REGISTER_CERTIFICATE)
        .resultCode("ERROR")
        .matchCriteria(matchCriteria)
        .triggerCount(0)
        .createdAt(createdAt)
        .build();
  }

  private MatchContext context(String logicalAddress, String certificateId, String personId) {
    return MatchContext.builder()
        .logicalAddress(logicalAddress)
        .certificateId(certificateId)
        .personId(personId)
        .build();
  }

  @Test
  void wildcardRuleMatchesAnyContext() {
    repository.save(rule(null));

    final var result =
        evaluator.evaluate(
            ServiceName.REGISTER_CERTIFICATE, context("any-address", "any-cert", "any-person"));

    assertTrue(result.isPresent());
  }

  @Test
  void wildcardRuleWithEmptyCriteriaMatchesAnyContext() {
    repository.save(rule(criteria(null, null, null)));

    final var result =
        evaluator.evaluate(
            ServiceName.REGISTER_CERTIFICATE, context("any-address", "any-cert", "any-person"));

    assertTrue(result.isPresent());
  }

  @Test
  void certificateIdRuleDoesNotMatchDifferentId() {
    repository.save(rule(criteria(null, "cert-123", null)));

    final var result =
        evaluator.evaluate(ServiceName.REGISTER_CERTIFICATE, context("any", "cert-456", null));

    assertTrue(result.isEmpty());
  }

  @Test
  void certificateIdRuleMatchesSameId() {
    final var rule = rule(criteria(null, "cert-123", null));
    repository.save(rule);

    final var result =
        evaluator.evaluate(ServiceName.REGISTER_CERTIFICATE, context("any", "cert-123", null));

    assertTrue(result.isPresent());
    assertEquals(rule.getId(), result.get().getId());
  }

  @Test
  void moreSpecificRuleWinsOverLessSpecific() {
    final var wildcard = rule(null);
    final var specific = rule(criteria(null, "cert-123", null));
    repository.save(wildcard);
    repository.save(specific);

    final var result =
        evaluator.evaluate(ServiceName.REGISTER_CERTIFICATE, context("any", "cert-123", null));

    assertTrue(result.isPresent());
    assertEquals(specific.getId(), result.get().getId());
  }

  @Test
  void mostRecentCreatedAtWinsTie() {
    final var older = rule(null, Instant.now().minusSeconds(10));
    final var newer = rule(null, Instant.now());
    repository.save(older);
    repository.save(newer);

    final var result =
        evaluator.evaluate(ServiceName.REGISTER_CERTIFICATE, context("any", "any", null));

    assertTrue(result.isPresent());
    assertEquals(newer.getId(), result.get().getId());
  }

  @Test
  void delayApplierCalledWhenDelayMillisSet() {
    final var rule =
        BehaviorRule.builder()
            .id(UUID.randomUUID())
            .serviceName(ServiceName.REGISTER_CERTIFICATE)
            .resultCode("ERROR")
            .delayMillis(50L)
            .triggerCount(0)
            .createdAt(Instant.now())
            .build();
    repository.save(rule);

    evaluator.evaluate(ServiceName.REGISTER_CERTIFICATE, context("any", "any", null));

    verify(delayApplier).apply(50L);
  }

  @Test
  void delayApplierNotCalledWhenDelayMillisNull() {
    repository.save(rule(null));

    evaluator.evaluate(ServiceName.REGISTER_CERTIFICATE, context("any", "any", null));

    verify(delayApplier, never()).apply(any(long.class));
  }

  @Test
  void incrementTriggerCountCalledOnMatch() {
    final var rule = rule(null);
    repository.save(rule);

    evaluator.evaluate(ServiceName.REGISTER_CERTIFICATE, context("any", "any", null));

    assertEquals(1, repository.findById(rule.getId()).get().getTriggerCount());
  }

  @Test
  void noMatchReturnsEmpty() {
    repository.save(rule(criteria(null, "cert-999", null)));

    final var result =
        evaluator.evaluate(ServiceName.REGISTER_CERTIFICATE, context("any", "cert-123", null));

    assertTrue(result.isEmpty());
  }

  @Test
  void personIdMatchingStripsHyphens() {
    repository.save(rule(criteria(null, null, "191212121212")));

    final var result =
        evaluator.evaluate(
            ServiceName.REGISTER_CERTIFICATE, context("any", "any", "19121212-1212"));

    assertTrue(result.isPresent());
  }

  @Test
  void differentServiceNameDoesNotMatch() {
    repository.save(rule(null));

    final var result =
        evaluator.evaluate(ServiceName.REVOKE_CERTIFICATE, context("any", "any", null));

    assertTrue(result.isEmpty());
  }
}
