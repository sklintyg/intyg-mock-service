package se.inera.intyg.intygmockservice.application.behavior.service;

import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.domain.EvaluationResult;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ErrorIdType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Component
public class CertificateBehaviorResponseBuilder {

  public RegisterCertificateResponseType build(EvaluationResult result) {
    final var response = new RegisterCertificateResponseType();
    final var resultType = new ResultType();
    resultType.setResultCode(ResultCodeType.valueOf(result.getResultCode()));
    if (result.getErrorId() != null) {
      resultType.setErrorId(ErrorIdType.valueOf(result.getErrorId()));
    }
    if (result.getResultText() != null) {
      resultType.setResultText(result.getResultText());
    }
    response.setResult(resultType);
    return response;
  }
}
