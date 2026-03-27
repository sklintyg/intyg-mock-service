package se.inera.intyg.intygmockservice.domain.navigation.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Staff {

  String staffId;
  String fullName;
  String prescriptionCode;
  Unit unit;
}
