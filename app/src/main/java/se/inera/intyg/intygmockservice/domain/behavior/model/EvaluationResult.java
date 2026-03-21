package se.inera.intyg.intygmockservice.domain.behavior.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EvaluationResult {
  String resultCode;
  String errorId;
  String resultText;
}
