package se.inera.intyg.intygmockservice.application.behavior.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.application.behavior.dto.CreateBehaviorRuleRequest;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;
import se.inera.intyg.intygmockservice.domain.behavior.repository.BehaviorRuleRepository;

@ExtendWith(MockitoExtension.class)
class BehaviorServiceTest {

  @Mock private BehaviorRuleRepository repository;

  @InjectMocks private BehaviorService behaviorService;

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
}
