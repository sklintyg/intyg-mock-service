package se.inera.intyg.intygmockservice.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.MatchContext;
import se.inera.intyg.intygmockservice.domain.MatchCriteria;
import se.inera.intyg.intygmockservice.domain.ServiceName;

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
  void shouldFindBestMatchBySpecificity() {
    final var wildcard =
        BehaviorRule.builder()
            .id(UUID.randomUUID())
            .serviceName(ServiceName.REGISTER_CERTIFICATE)
            .resultCode("ERROR")
            .triggerCount(0)
            .createdAt(Instant.now())
            .build();
    final var specific =
        BehaviorRule.builder()
            .id(UUID.randomUUID())
            .serviceName(ServiceName.REGISTER_CERTIFICATE)
            .resultCode("ERROR")
            .matchCriteria(MatchCriteria.builder().certificateId("cert-123").build())
            .triggerCount(0)
            .createdAt(Instant.now())
            .build();
    repository.save(wildcard);
    repository.save(specific);

    final var context =
        MatchContext.builder().logicalAddress("any").certificateId("cert-123").build();
    final var result = repository.findBestMatch(ServiceName.REGISTER_CERTIFICATE, context);

    assertTrue(result.isPresent());
    assertEquals(specific.getId(), result.get().getId());
  }

  @Test
  void shouldFindBestMatchByMostRecentCreatedAt() {
    final var older =
        BehaviorRule.builder()
            .id(UUID.randomUUID())
            .serviceName(ServiceName.REGISTER_CERTIFICATE)
            .resultCode("ERROR")
            .triggerCount(0)
            .createdAt(Instant.now().minusSeconds(10))
            .build();
    final var newer =
        BehaviorRule.builder()
            .id(UUID.randomUUID())
            .serviceName(ServiceName.REGISTER_CERTIFICATE)
            .resultCode("ERROR")
            .triggerCount(0)
            .createdAt(Instant.now())
            .build();
    repository.save(older);
    repository.save(newer);

    final var context = MatchContext.builder().logicalAddress("any").certificateId("any").build();
    final var result = repository.findBestMatch(ServiceName.REGISTER_CERTIFICATE, context);

    assertTrue(result.isPresent());
    assertEquals(newer.getId(), result.get().getId());
  }

  @Test
  void shouldReturnEmptyWhenNoMatch() {
    final var rule =
        BehaviorRule.builder()
            .id(UUID.randomUUID())
            .serviceName(ServiceName.REGISTER_CERTIFICATE)
            .resultCode("ERROR")
            .matchCriteria(MatchCriteria.builder().certificateId("cert-999").build())
            .triggerCount(0)
            .createdAt(Instant.now())
            .build();
    repository.save(rule);

    final var context =
        MatchContext.builder().logicalAddress("any").certificateId("cert-123").build();
    final var result = repository.findBestMatch(ServiceName.REGISTER_CERTIFICATE, context);

    assertTrue(result.isEmpty());
  }
}
