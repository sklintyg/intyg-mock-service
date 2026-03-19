package se.inera.intyg.intygmockservice.behavior;

import org.springframework.stereotype.Component;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ErrorIdType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Component
public class CertificateBehaviorResponseBuilder {

  public RegisterCertificateResponseType build(BehaviorRule rule) {
    final var response = new RegisterCertificateResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.valueOf(rule.getResultCode()));
    if (rule.getErrorId() != null) {
      result.setErrorId(ErrorIdType.valueOf(rule.getErrorId()));
    }
    if (rule.getResultText() != null) {
      result.setResultText(rule.getResultText());
    }
    response.setResult(result);
    return response;
  }
}
