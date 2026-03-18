package se.inera.intyg.intygmockservice.statusupdates;

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
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponseType;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@ExtendWith(MockitoExtension.class)
class CertificateStatusUpdateForCareResponderImplTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";

  @Mock private CertificateStatusUpdateForCareService service;

  @InjectMocks private CertificateStatusUpdateForCareResponderImpl responder;

  @Test
  void shouldReturnLocalOkWhenServiceReturnsEmpty() {
    final var type = new CertificateStatusUpdateForCareType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.empty());

    final var response = responder.certificateStatusUpdateForCare(LOGICAL_ADDRESS, type);

    assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
  }

  @Test
  void shouldReturnUpstreamResponseWhenServiceReturnsPresent() {
    final var upstreamResponse = okResponse();
    final var type = new CertificateStatusUpdateForCareType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.of(upstreamResponse));

    final var response = responder.certificateStatusUpdateForCare(LOGICAL_ADDRESS, type);

    assertSame(upstreamResponse, response);
  }

  @Test
  void shouldDelegateToService() {
    final var type = new CertificateStatusUpdateForCareType();
    when(service.store(LOGICAL_ADDRESS, type)).thenReturn(Optional.empty());

    responder.certificateStatusUpdateForCare(LOGICAL_ADDRESS, type);

    verify(service).store(LOGICAL_ADDRESS, type);
  }

  private CertificateStatusUpdateForCareResponseType okResponse() {
    final var response = new CertificateStatusUpdateForCareResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }
}
