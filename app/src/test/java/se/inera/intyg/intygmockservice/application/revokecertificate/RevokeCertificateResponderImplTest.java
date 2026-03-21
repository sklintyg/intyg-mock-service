package se.inera.intyg.intygmockservice.application.revokecertificate;

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
import se.inera.intyg.intygmockservice.application.revokecertificate.service.RevokeCertificateService;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@ExtendWith(MockitoExtension.class)
class RevokeCertificateResponderImplTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";

  @Mock private RevokeCertificateService service;

  @InjectMocks private RevokeCertificateResponderImpl responder;

  @Test
  void shouldReturnLocalOkWhenServiceReturnsEmpty() {
    final var type = new RevokeCertificateType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.empty());

    final var response = responder.revokeCertificate(LOGICAL_ADDRESS, type);

    assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
  }

  @Test
  void shouldReturnUpstreamResponseWhenServiceReturnsPresent() {
    final var upstreamResponse = okResponse();
    final var type = new RevokeCertificateType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.of(upstreamResponse));

    final var response = responder.revokeCertificate(LOGICAL_ADDRESS, type);

    assertSame(upstreamResponse, response);
  }

  @Test
  void shouldDelegateToService() {
    final var type = new RevokeCertificateType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.empty());

    responder.revokeCertificate(LOGICAL_ADDRESS, type);

    verify(service).store(LOGICAL_ADDRESS, type);
  }

  private RevokeCertificateResponseType okResponse() {
    final var response = new RevokeCertificateResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }
}
