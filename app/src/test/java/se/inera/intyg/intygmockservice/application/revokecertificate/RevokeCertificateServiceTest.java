package se.inera.intyg.intygmockservice.application.revokecertificate;

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
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.application.common.dto.PatientDTO.PersonId;
import se.inera.intyg.intygmockservice.application.revokecertificate.converter.RevokeCertificateConverter;
import se.inera.intyg.intygmockservice.application.revokecertificate.dto.RevokeCertificateDTO;
import se.inera.intyg.intygmockservice.application.revokecertificate.service.RevokeCertificateResponseFactory;
import se.inera.intyg.intygmockservice.application.revokecertificate.service.RevokeCertificateService;
import se.inera.intyg.intygmockservice.domain.behavior.model.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.behavior.model.MockResponse;
import se.inera.intyg.intygmockservice.domain.behavior.repository.BehaviorRuleRepository;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.RevokeCertificatePassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.RevokeCertificateRepository;
import se.inera.intyg.intygmockservice.infrastructure.xml.JaxbXmlMarshaller;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RevokeCertificateServiceTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";
  private static final String CERTIFICATE_ID = "cert-123";

  @Mock private RevokeCertificateRepository repository;
  @Mock private RevokeCertificateConverter converter;
  @Mock private RevokeCertificatePassthroughClient passthroughClient;
  @Mock private BehaviorRuleRepository behaviorRuleRepository;
  @Mock private RevokeCertificateResponseFactory responseFactory;
  @Mock private JaxbXmlMarshaller xmlMarshaller;

  @InjectMocks private RevokeCertificateService service;

  @BeforeEach
  void setUp() {
    when(converter.convert(any())).thenReturn(buildDto());
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.empty());
    when(behaviorRuleRepository.findBestMatch(any(), any())).thenReturn(Optional.empty());
  }

  @Test
  void shouldAddToRepositoryWhenStore() {
    final var type = new RevokeCertificateType();

    service.store(LOGICAL_ADDRESS, type);

    verify(repository).add(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldDelegateToPassthroughClientWhenStore() {
    final var type = new RevokeCertificateType();

    service.store(LOGICAL_ADDRESS, type);

    verify(passthroughClient).forward(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldReturnPassthroughResultWhenStore() {
    final var type = new RevokeCertificateType();
    final var response = okResponse();
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.of(response));

    final var result = service.store(LOGICAL_ADDRESS, type);

    assertTrue(result.isPresent());
    assertEquals(response, result.get());
  }

  @Test
  void shouldReturnEmptyOptionalWhenPassthroughDisabled() {
    final var result = service.store(LOGICAL_ADDRESS, new RevokeCertificateType());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnErrorResponseAndSkipStorageWhenErrorRuleMatches() {
    final var rule = errorRule();
    final var errorResponse = errorResponse();
    when(behaviorRuleRepository.findBestMatch(any(), any())).thenReturn(Optional.of(rule));
    when(responseFactory.create(any(MockResponse.class))).thenReturn(errorResponse);

    final var result = service.store(LOGICAL_ADDRESS, new RevokeCertificateType());

    assertTrue(result.isPresent());
    assertEquals(errorResponse, result.get());
    verify(repository, never()).add(any(), any());
    verify(passthroughClient, never()).forward(any(), any());
  }

  @Test
  void shouldStoreNormallyWhenDelayOnlyRuleMatches() {
    final var rule = delayOnlyRule();
    when(behaviorRuleRepository.findBestMatch(any(), any())).thenReturn(Optional.of(rule));

    service.store(LOGICAL_ADDRESS, new RevokeCertificateType());

    verify(repository).add(any(), any());
  }

  @Test
  void shouldReturnAllWhenGetAll() {
    final var type = new RevokeCertificateType();
    final var dto = RevokeCertificateDTO.builder().build();
    when(repository.findAll()).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getAll();

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldReturnDtoWhenGetByIdExists() {
    final var type = new RevokeCertificateType();
    final var dto = RevokeCertificateDTO.builder().build();
    when(repository.findByCertificateId(CERTIFICATE_ID)).thenReturn(Optional.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getById(CERTIFICATE_ID);

    assertTrue(result.isPresent());
    assertEquals(dto, result.get());
  }

  @Test
  void shouldReturnEmptyWhenGetByIdNotFound() {
    when(repository.findByCertificateId(CERTIFICATE_ID)).thenReturn(Optional.empty());

    final var result = service.getById(CERTIFICATE_ID);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnDtosWhenGetByLogicalAddress() {
    final var type = new RevokeCertificateType();
    final var dto = RevokeCertificateDTO.builder().build();
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
    final var type = new RevokeCertificateType();
    final var dto = RevokeCertificateDTO.builder().build();
    when(repository.findByPersonId(any())).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByPersonId("191212121212");

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldDelegateToRepositoryWhenDeleteAll() {
    service.deleteAll();

    verify(repository).deleteAll();
  }

  @Test
  void shouldDelegateToRepositoryWhenDeleteById() {
    service.deleteById(CERTIFICATE_ID);

    verify(repository).deleteById(CERTIFICATE_ID);
  }

  private RevokeCertificateDTO buildDto() {
    return RevokeCertificateDTO.builder()
        .intygsId(IntygsId.builder().root("root").extension(CERTIFICATE_ID).build())
        .patientPersonId(PersonId.builder().root("root").extension("191212121212").build())
        .meddelande("reason")
        .build();
  }

  private BehaviorRule errorRule() {
    final var rule = mock(BehaviorRule.class);
    when(rule.evaluate(any()))
        .thenReturn(
            Optional.of(
                MockResponse.builder().resultCode("ERROR").errorId("VALIDATION_ERROR").build()));
    return rule;
  }

  private BehaviorRule delayOnlyRule() {
    final var rule = mock(BehaviorRule.class);
    when(rule.evaluate(any())).thenReturn(Optional.empty());
    return rule;
  }

  private RevokeCertificateResponseType okResponse() {
    final var response = new RevokeCertificateResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }

  private RevokeCertificateResponseType errorResponse() {
    final var response = new RevokeCertificateResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.ERROR);
    response.setResult(result);
    return response;
  }
}
