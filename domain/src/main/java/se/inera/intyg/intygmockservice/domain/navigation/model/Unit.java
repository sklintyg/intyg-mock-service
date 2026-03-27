package se.inera.intyg.intygmockservice.domain.navigation.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Unit {

  String unitId;
  String unitName;
  String streetAddress;
  String postalCode;
  String city;
  String phone;
  String email;
  CareProvider careProvider;
}
