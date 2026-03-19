package se.inera.intyg.intygmockservice.application.common.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MeddelandeReferensDTO {

  private String meddelandeId;
  private String referensId;
}
