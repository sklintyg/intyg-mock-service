package se.inera.intyg.intygmockservice.common.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PatientDTO {

  PersonId personId;
  String fornamn;
  String efternamn;
  String postadress;
  String postnummer;
  String postort;

  @Value
  @Builder
  public static class PersonId {

    String root;
    String extension;
  }
}
