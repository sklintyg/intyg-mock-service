package se.inera.intyg.intygmockservice.application.storelog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import se.inera.intyg.intygmockservice.application.storelog.converter.StoreLogTypeConverter;
import se.inera.intyg.intygmockservice.domain.behavior.model.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.behavior.model.MockResponse;
import se.inera.intyg.intygmockservice.domain.behavior.repository.BehaviorRuleRepository;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.StoreLogPassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.StoreLogTypeRepository;
import se.inera.intyg.intygmockservice.infrastructure.xml.JaxbXmlMarshaller;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogResponseType;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;
import se.riv.informationsecurity.auditing.log.v2.ActivityType;
import se.riv.informationsecurity.auditing.log.v2.LogType;
import se.riv.informationsecurity.auditing.log.v2.ResultCodeType;
import se.riv.informationsecurity.auditing.log.v2.ResultType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StoreLogServiceTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";

  @Mock private StoreLogTypeRepository repository;
  @Mock private StoreLogTypeConverter converter;
  @Mock private StoreLogPassthroughClient passthroughClient;
  @Mock private BehaviorRuleRepository behaviorRuleRepository;
  @Mock private StoreLogResponseFactory responseFactory;
  @Mock private JaxbXmlMarshaller xmlMarshaller;

  @InjectMocks private StoreLogService service;

  @BeforeEach
  void setUp() {
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.empty());
    when(behaviorRuleRepository.findBestMatch(any(), any())).thenReturn(Optional.empty());
  }

  @Test
  void shouldAddToRepositoryWhenStore() {
    final var type = new StoreLogType();

    service.store(LOGICAL_ADDRESS, type);

    verify(repository).add(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldDelegateToPassthroughClientWhenStore() {
    final var type = new StoreLogType();

    service.store(LOGICAL_ADDRESS, type);

    verify(passthroughClient).forward(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldReturnPassthroughResultWhenStore() {
    final var type = new StoreLogType();
    final var response = okResponse();
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.of(response));

    final var result = service.store(LOGICAL_ADDRESS, type);

    assertTrue(result.isPresent());
    assertEquals(response, result.get());
  }

  @Test
  void shouldReturnEmptyOptionalWhenPassthroughDisabled() {
    final var result = service.store(LOGICAL_ADDRESS, new StoreLogType());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnErrorResponseAndSkipStorageWhenErrorRuleMatches() {
    final var rule = errorRule();
    final var errorResponse = errorResponse();
    when(behaviorRuleRepository.findBestMatch(any(), any())).thenReturn(Optional.of(rule));
    when(responseFactory.create(any(MockResponse.class))).thenReturn(errorResponse);

    final var result = service.store(LOGICAL_ADDRESS, storeLogTypeWithCertificate("cert-001"));

    assertTrue(result.isPresent());
    assertEquals(errorResponse, result.get());
    verify(repository, never()).add(any(), any());
    verify(passthroughClient, never()).forward(any(), any());
  }

  @Test
  void shouldStoreNormallyWhenDelayOnlyRuleMatches() {
    final var rule = delayOnlyRule();
    when(behaviorRuleRepository.findBestMatch(any(), any())).thenReturn(Optional.of(rule));

    service.store(LOGICAL_ADDRESS, storeLogTypeWithCertificate("cert-001"));

    verify(repository).add(any(), any());
  }

  private static StoreLogType storeLogTypeWithCertificate(String certificateId) {
    final var activity = new ActivityType();
    activity.setActivityLevel(certificateId);
    final var log = new LogType();
    log.setActivity(activity);
    final var type = new StoreLogType();
    type.getLog().add(log);
    return type;
  }

  private BehaviorRule errorRule() {
    final var rule = mock(BehaviorRule.class);
    when(rule.evaluate(any()))
        .thenReturn(Optional.of(MockResponse.builder().resultCode("VALIDATION_ERROR").build()));
    return rule;
  }

  private BehaviorRule delayOnlyRule() {
    final var rule = mock(BehaviorRule.class);
    when(rule.evaluate(any())).thenReturn(Optional.empty());
    return rule;
  }

  private StoreLogResponseType okResponse() {
    final var response = new StoreLogResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }

  private StoreLogResponseType errorResponse() {
    final var response = new StoreLogResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.VALIDATION_ERROR);
    response.setResult(result);
    return response;
  }
}
