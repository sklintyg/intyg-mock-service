package se.inera.intyg.intygmockservice.application.revokecertificate.service;

import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.domain.behavior.model.EvaluationResult;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ErrorIdType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Component
public class RevokeCertificateResponseFactory {

  public RevokeCertificateResponseType create(EvaluationResult result) {
    final var response = new RevokeCertificateResponseType();
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
