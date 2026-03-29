package se.inera.intyg.intygmockservice.application.registercertificate.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.application.registercertificate.service.RegisterCertificateService;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@ExtendWith(MockitoExtension.class)
class RegisterCertificateResponderImplTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";

  @Mock private RegisterCertificateService service;

  @InjectMocks private RegisterCertificateResponderImpl responder;

  @Test
  void shouldReturnLocalOkWhenServiceReturnsEmpty() {
    final var type = new RegisterCertificateType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.empty());

    final var response = responder.registerCertificate(LOGICAL_ADDRESS, type);

    assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
  }

  @Test
  void shouldReturnUpstreamResponseWhenServiceReturnsPresent() {
    final var upstreamResponse = okResponse();
    final var type = new RegisterCertificateType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.of(upstreamResponse));

    final var response = responder.registerCertificate(LOGICAL_ADDRESS, type);

    assertSame(upstreamResponse, response);
  }

  @Test
  void shouldDelegateToService() {
    final var type = new RegisterCertificateType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.empty());

    responder.registerCertificate(LOGICAL_ADDRESS, type);

    verify(service).store(LOGICAL_ADDRESS, type);
  }

  private RegisterCertificateResponseType okResponse() {
    final var response = new RegisterCertificateResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }
}
