package se.inera.intyg.intygmockservice.domain.behavior.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MatchContext {
  String logicalAddress;
  String certificateId;
  String personId;
}
