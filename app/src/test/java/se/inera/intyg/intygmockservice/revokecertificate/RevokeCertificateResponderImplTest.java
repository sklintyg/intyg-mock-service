package se.inera.intyg.intygmockservice.revokecertificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;

@ExtendWith(MockitoExtension.class)
class RevokeCertificateResponderImplTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";

  @Mock private RevokeCertificateService service;

  @InjectMocks private RevokeCertificateResponderImpl responder;

  @Test
  void shouldDelegateToServiceWhenRevokeCertificate() {
    final var type = new RevokeCertificateType();

    responder.revokeCertificate(LOGICAL_ADDRESS, type);

    verify(service).store(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldReturnOkResultWhenRevokeCertificate() {
    final var type = new RevokeCertificateType();

    final var response = responder.revokeCertificate(LOGICAL_ADDRESS, type);

    assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
  }
}
