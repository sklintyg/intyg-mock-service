package se.inera.intyg.intygmockservice.common.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CodeTypeDTO {

  String code;
  String codeSystem;
  String displayName;
}
