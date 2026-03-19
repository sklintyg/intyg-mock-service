package se.inera.intyg.intygmockservice.application.revokecertificate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Service
@RequiredArgsConstructor
public class RevokeCertificateResponderImpl implements RevokeCertificateResponderInterface {

  private final RevokeCertificateService service;

  @Override
  public RevokeCertificateResponseType revokeCertificate(
      String logicalAddress, RevokeCertificateType revokeCertificate) {
    return service
        .store(logicalAddress, revokeCertificate)
        .orElseGet(
            () -> {
              final var response = new RevokeCertificateResponseType();
              final var result = new ResultType();
              result.setResultCode(ResultCodeType.OK);
              response.setResult(result);
              return response;
            });
  }
}
