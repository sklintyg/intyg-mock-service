package se.inera.intyg.intygmockservice.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EvaluationResult {
  String resultCode;
  String errorId;
  String resultText;
}
