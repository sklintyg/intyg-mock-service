package se.inera.intyg.intygmockservice.registercertificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.registercertificate.converter.RegisterCertificateConverter;
import se.inera.intyg.intygmockservice.registercertificate.dto.RegisterCertificateDTO;
import se.inera.intyg.intygmockservice.registercertificate.passthrough.RegisterCertificatePassthroughClient;
import se.inera.intyg.intygmockservice.registercertificate.repository.RegisterCertificateRepository;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@ExtendWith(MockitoExtension.class)
class RegisterCertificateServiceTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";

  @Mock private RegisterCertificateRepository repository;
  @Mock private RegisterCertificateConverter converter;
  @Mock private RegisterCertificatePassthroughClient passthroughClient;

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

  private RegisterCertificateResponseType okResponse() {
    final var response = new RegisterCertificateResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }
}
