package se.inera.intyg.intygmockservice.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BehaviorRuleRepositoryTest {

  private BehaviorRuleRepository repository;

  @BeforeEach
  void setUp() {
    repository = new BehaviorRuleRepository();
  }

  private BehaviorRule rule(ServiceName serviceName) {
    return BehaviorRule.builder()
        .id(UUID.randomUUID())
        .serviceName(serviceName)
        .resultCode("ERROR")
        .triggerCount(0)
        .createdAt(Instant.now())
        .build();
  }

  @Test
  void shouldSaveAndFindById() {
    final var rule = rule(ServiceName.REGISTER_CERTIFICATE);
    repository.save(rule);

    assertTrue(repository.findById(rule.getId()).isPresent());
    assertEquals(rule, repository.findById(rule.getId()).get());
  }

  @Test
  void shouldReturnEmptyForUnknownId() {
    assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
  }

  @Test
  void shouldFindAll() {
    repository.save(rule(ServiceName.REGISTER_CERTIFICATE));
    repository.save(rule(ServiceName.REVOKE_CERTIFICATE));

    assertEquals(2, repository.findAll().size());
  }

  @Test
  void shouldFindByServiceName() {
    repository.save(rule(ServiceName.REGISTER_CERTIFICATE));
    repository.save(rule(ServiceName.REVOKE_CERTIFICATE));

    final var found = repository.findByServiceName(ServiceName.REGISTER_CERTIFICATE);

    assertEquals(1, found.size());
    assertEquals(ServiceName.REGISTER_CERTIFICATE, found.get(0).getServiceName());
  }

  @Test
  void shouldReturnEmptyListForUnknownService() {
    repository.save(rule(ServiceName.REGISTER_CERTIFICATE));

    assertTrue(repository.findByServiceName(ServiceName.STORE_LOG).isEmpty());
  }

  @Test
  void shouldDeleteById() {
    final var rule = rule(ServiceName.REGISTER_CERTIFICATE);
    repository.save(rule);

    final var deleted = repository.delete(rule.getId());

    assertTrue(deleted);
    assertTrue(repository.findById(rule.getId()).isEmpty());
  }

  @Test
  void shouldReturnFalseWhenDeletingUnknownId() {
    assertFalse(repository.delete(UUID.randomUUID()));
  }

  @Test
  void shouldDeleteAll() {
    repository.save(rule(ServiceName.REGISTER_CERTIFICATE));
    repository.save(rule(ServiceName.REVOKE_CERTIFICATE));

    repository.deleteAll();

    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  void shouldDeleteByServiceName() {
    final var rcRule = rule(ServiceName.REGISTER_CERTIFICATE);
    final var rvRule = rule(ServiceName.REVOKE_CERTIFICATE);
    repository.save(rcRule);
    repository.save(rvRule);

    repository.deleteByServiceName(ServiceName.REGISTER_CERTIFICATE);

    assertTrue(repository.findByServiceName(ServiceName.REGISTER_CERTIFICATE).isEmpty());
    assertEquals(1, repository.findByServiceName(ServiceName.REVOKE_CERTIFICATE).size());
  }

  @Test
  void shouldIncrementTriggerCount() {
    final var rule = rule(ServiceName.REGISTER_CERTIFICATE);
    repository.save(rule);

    repository.incrementTriggerCount(rule.getId());

    assertEquals(1, repository.findById(rule.getId()).get().getTriggerCount());
  }

  @Test
  void shouldAutoRemoveWhenMaxTriggerCountReached() {
    final var rule =
        BehaviorRule.builder()
            .id(UUID.randomUUID())
            .serviceName(ServiceName.REGISTER_CERTIFICATE)
            .resultCode("ERROR")
            .maxTriggerCount(1)
            .triggerCount(0)
            .createdAt(Instant.now())
            .build();
    repository.save(rule);

    repository.incrementTriggerCount(rule.getId());

    assertTrue(repository.findById(rule.getId()).isEmpty());
  }

  @Test
  void shouldNotAutoRemoveWhenMaxTriggerCountNotYetReached() {
    final var rule =
        BehaviorRule.builder()
            .id(UUID.randomUUID())
            .serviceName(ServiceName.REGISTER_CERTIFICATE)
            .resultCode("ERROR")
            .maxTriggerCount(2)
            .triggerCount(0)
            .createdAt(Instant.now())
            .build();
    repository.save(rule);

    repository.incrementTriggerCount(rule.getId());

    assertTrue(repository.findById(rule.getId()).isPresent());
  }
}
