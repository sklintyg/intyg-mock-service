package se.inera.intyg.intygmockservice.application.registercertificate;

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
import se.inera.intyg.intygmockservice.application.behavior.service.CertificateBehaviorResponseBuilder;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.application.registercertificate.converter.RegisterCertificateConverter;
import se.inera.intyg.intygmockservice.application.registercertificate.dto.RegisterCertificateDTO;
import se.inera.intyg.intygmockservice.domain.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.EvaluationResult;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.RegisterCertificatePassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.BehaviorRuleRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.RegisterCertificateRepository;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegisterCertificateServiceTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";

  @Mock private RegisterCertificateRepository repository;
  @Mock private RegisterCertificateConverter converter;
  @Mock private RegisterCertificatePassthroughClient passthroughClient;
  @Mock private BehaviorRuleRepository behaviorRuleRepository;
  @Mock private CertificateBehaviorResponseBuilder responseBuilder;

  @InjectMocks private RegisterCertificateService service;

  @BeforeEach
  void setUp() {
    final var dto =
        RegisterCertificateDTO.builder()
            .intyg(
                IntygDTO.builder()
                    .intygsId(IntygDTO.IntygsId.builder().extension("test-cert-id").build())
                    .build())
            .build();
    when(converter.convert(any())).thenReturn(dto);
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.empty());
    when(behaviorRuleRepository.findBestMatch(any(), any())).thenReturn(Optional.empty());
  }

  @Test
  void shouldAddToRepositoryWhenStore() {
    final var type = new RegisterCertificateType();
    service.store(LOGICAL_ADDRESS, type);

    verify(repository).add(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldDelegateToPassthroughClientWhenStore() {
    final var type = new RegisterCertificateType();
    service.store(LOGICAL_ADDRESS, type);

    verify(passthroughClient).forward(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldReturnPassthroughResultWhenStore() {
    final var response = okResponse();
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.of(response));

    final var result = service.store(LOGICAL_ADDRESS, new RegisterCertificateType());

    assertTrue(result.isPresent());
    assertEquals(response, result.get());
  }

  @Test
  void shouldReturnEmptyOptionalWhenPassthroughDisabled() {
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.empty());

    final var result = service.store(LOGICAL_ADDRESS, new RegisterCertificateType());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnErrorResponseAndSkipStorageWhenErrorRuleMatches() {
    final var rule = errorRule();
    final var errorResponse = errorResponse();
    when(behaviorRuleRepository.findBestMatch(any(), any())).thenReturn(Optional.of(rule));
    when(responseBuilder.build(any(EvaluationResult.class))).thenReturn(errorResponse);

    final var result = service.store(LOGICAL_ADDRESS, new RegisterCertificateType());

    assertTrue(result.isPresent());
    assertEquals(errorResponse, result.get());
    verify(repository, never()).add(any(), any());
    verify(passthroughClient, never()).forward(any(), any());
  }

  @Test
  void shouldStoreNormallyWhenDelayOnlyRuleMatches() {
    final var rule = delayOnlyRule();
    when(behaviorRuleRepository.findBestMatch(any(), any())).thenReturn(Optional.of(rule));

    service.store(LOGICAL_ADDRESS, new RegisterCertificateType());

    verify(repository).add(any(), any());
  }

  private BehaviorRule errorRule() {
    final var rule = mock(BehaviorRule.class);
    when(rule.evaluate(any()))
        .thenReturn(
            Optional.of(
                EvaluationResult.builder()
                    .resultCode("ERROR")
                    .errorId("VALIDATION_ERROR")
                    .build()));
    return rule;
  }

  private BehaviorRule delayOnlyRule() {
    final var rule = mock(BehaviorRule.class);
    when(rule.evaluate(any())).thenReturn(Optional.empty());
    return rule;
  }

  private RegisterCertificateResponseType okResponse() {
    final var response = new RegisterCertificateResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }

  private RegisterCertificateResponseType errorResponse() {
    final var response = new RegisterCertificateResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.ERROR);
    response.setResult(result);
    return response;
  }
}
