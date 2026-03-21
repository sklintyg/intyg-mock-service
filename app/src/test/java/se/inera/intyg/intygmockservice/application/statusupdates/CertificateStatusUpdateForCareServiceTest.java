package se.inera.intyg.intygmockservice.application.statusupdates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.application.common.dto.PatientDTO;
import se.inera.intyg.intygmockservice.application.common.dto.PatientDTO.PersonId;
import se.inera.intyg.intygmockservice.application.statusupdates.converter.CertificateStatusUpdateForCareConverter;
import se.inera.intyg.intygmockservice.application.statusupdates.dto.CertificateStatusUpdateForCareDTO;
import se.inera.intyg.intygmockservice.application.statusupdates.dto.CertificateStatusUpdateForCareDTO.Handelse;
import se.inera.intyg.intygmockservice.application.statusupdates.dto.CertificateStatusUpdateForCareDTO.Handelse.Handelsekod;
import se.inera.intyg.intygmockservice.application.statusupdates.service.CertificateStatusUpdateForCareResponseFactory;
import se.inera.intyg.intygmockservice.domain.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.EvaluationResult;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.CertificateStatusUpdateForCarePassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.BehaviorRuleRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.CertificateStatusUpdateForCareRepository;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponseType;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CertificateStatusUpdateForCareServiceTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";
  private static final String CERTIFICATE_ID = "cert-123";
  private static final String PERSON_ID = "191212121212";
  private static final String EVENT_CODE = "SKAPAT";

  @Mock private CertificateStatusUpdateForCareRepository repository;
  @Mock private CertificateStatusUpdateForCareConverter converter;
  @Mock private CertificateStatusUpdateForCarePassthroughClient passthroughClient;
  @Mock private BehaviorRuleRepository behaviorRuleRepository;
  @Mock private CertificateStatusUpdateForCareResponseFactory responseFactory;

  @InjectMocks private CertificateStatusUpdateForCareService service;

  @BeforeEach
  void setUp() {
    when(converter.convert(any())).thenReturn(buildDto(CERTIFICATE_ID));
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.empty());
    when(behaviorRuleRepository.findBestMatch(any(), any())).thenReturn(Optional.empty());
  }

  @Test
  void shouldAddToRepositoryWhenStore() {
    final var type = new CertificateStatusUpdateForCareType();

    service.store(LOGICAL_ADDRESS, type);

    verify(repository).add(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldDelegateToPassthroughClientWhenStore() {
    final var type = new CertificateStatusUpdateForCareType();

    service.store(LOGICAL_ADDRESS, type);

    verify(passthroughClient).forward(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldReturnPassthroughResultWhenStore() {
    final var type = new CertificateStatusUpdateForCareType();
    final var response = okResponse();
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.of(response));

    final var result = service.store(LOGICAL_ADDRESS, type);

    assertTrue(result.isPresent());
    assertEquals(response, result.get());
  }

  @Test
  void shouldReturnEmptyOptionalWhenPassthroughDisabled() {
    final var result = service.store(LOGICAL_ADDRESS, new CertificateStatusUpdateForCareType());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnErrorResponseAndSkipStorageWhenErrorRuleMatches() {
    final var rule = errorRule();
    final var errorResponse = errorResponse();
    when(behaviorRuleRepository.findBestMatch(any(), any())).thenReturn(Optional.of(rule));
    when(responseFactory.create(any(EvaluationResult.class))).thenReturn(errorResponse);

    final var result = service.store(LOGICAL_ADDRESS, new CertificateStatusUpdateForCareType());

    assertTrue(result.isPresent());
    assertEquals(errorResponse, result.get());
    verify(repository, never()).add(any(), any());
    verify(passthroughClient, never()).forward(any(), any());
  }

  @Test
  void shouldStoreNormallyWhenDelayOnlyRuleMatches() {
    final var rule = delayOnlyRule();
    when(behaviorRuleRepository.findBestMatch(any(), any())).thenReturn(Optional.of(rule));

    service.store(LOGICAL_ADDRESS, new CertificateStatusUpdateForCareType());

    verify(repository).add(any(), any());
  }

  @Test
  void shouldReturnAllWhenGetAll() {
    final var type = new CertificateStatusUpdateForCareType();
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(repository.findAll()).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getAll();

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldReturnDtosWhenGetByCertificateId() {
    final var type = new CertificateStatusUpdateForCareType();
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(repository.findByCertificateId(CERTIFICATE_ID)).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByCertificateId(CERTIFICATE_ID);

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldReturnDtosWhenGetByLogicalAddress() {
    final var type = new CertificateStatusUpdateForCareType();
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(repository.findByLogicalAddress(LOGICAL_ADDRESS)).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByLogicalAddress(LOGICAL_ADDRESS);

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldNormalizePersonIdWhenGetByPersonId() {
    when(repository.findByPersonId("191212121212")).thenReturn(List.of());

    service.getByPersonId("19121212-1212");

    verify(repository).findByPersonId("191212121212");
  }

  @Test
  void shouldReturnDtosWhenGetByPersonId() {
    final var type = new CertificateStatusUpdateForCareType();
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(repository.findByPersonId(any())).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByPersonId(PERSON_ID);

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldReturnDtosWhenGetByEventCode() {
    final var type = new CertificateStatusUpdateForCareType();
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(repository.findByEventCode(EVENT_CODE)).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByEventCode(EVENT_CODE);

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldDelegateToRepositoryWhenDeleteAll() {
    service.deleteAll();

    verify(repository).deleteAll();
  }

  @Test
  void shouldDelegateToRepositoryWhenDeleteByCertificateId() {
    service.deleteByCertificateId(CERTIFICATE_ID);

    verify(repository).deleteByCertificateId(CERTIFICATE_ID);
  }

  private static CertificateStatusUpdateForCareDTO buildDto(String certificateId) {
    return CertificateStatusUpdateForCareDTO.builder()
        .intyg(
            IntygDTO.builder()
                .intygsId(IntygsId.builder().root("root").extension(certificateId).build())
                .patient(
                    PatientDTO.builder()
                        .personId(PersonId.builder().root("root").extension(PERSON_ID).build())
                        .build())
                .build())
        .handelse(
            Handelse.builder().handelsekod(Handelsekod.builder().code(EVENT_CODE).build()).build())
        .build();
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

  private CertificateStatusUpdateForCareResponseType okResponse() {
    final var response = new CertificateStatusUpdateForCareResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }

  private CertificateStatusUpdateForCareResponseType errorResponse() {
    final var response = new CertificateStatusUpdateForCareResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.ERROR);
    response.setResult(result);
    return response;
  }
}
