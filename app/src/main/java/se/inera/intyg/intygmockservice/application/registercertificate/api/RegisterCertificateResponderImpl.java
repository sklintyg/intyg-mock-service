package se.inera.intyg.intygmockservice.application.registercertificate.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.registercertificate.service.RegisterCertificateService;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Service
@RequiredArgsConstructor
public class RegisterCertificateResponderImpl implements RegisterCertificateResponderInterface {

  private final RegisterCertificateService service;

  @Override
  public RegisterCertificateResponseType registerCertificate(
      String logicalAddress, RegisterCertificateType registerCertificateType) {
    return service
        .store(logicalAddress, registerCertificateType)
        .orElseGet(
            () -> {
              final var response = new RegisterCertificateResponseType();
              final var result = new ResultType();
              result.setResultCode(ResultCodeType.OK);
              response.setResult(result);
              return response;
            });
  }
}
