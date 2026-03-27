package se.inera.intyg.intygmockservice.domain.navigation.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Patient {

  PersonId personId;
  String firstName;
  String lastName;
  String streetAddress;
  String postalCode;
  String city;
}
