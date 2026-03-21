package se.inera.intyg.intygmockservice.application.storelog.service;

import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.domain.behavior.model.EvaluationResult;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogResponseType;
import se.riv.informationsecurity.auditing.log.v2.ResultCodeType;
import se.riv.informationsecurity.auditing.log.v2.ResultType;

@Component
public class StoreLogResponseFactory {

  public StoreLogResponseType create(EvaluationResult result) {
    final var response = new StoreLogResponseType();
    final var resultType = new ResultType();
    resultType.setResultCode(ResultCodeType.valueOf(result.getResultCode()));
    if (result.getResultText() != null) {
      resultType.setResultText(result.getResultText());
    }
    response.setResult(resultType);
    return response;
  }
}
