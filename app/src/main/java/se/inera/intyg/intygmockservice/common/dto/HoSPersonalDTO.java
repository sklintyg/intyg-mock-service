package se.inera.intyg.intygmockservice.common.dto;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HoSPersonalDTO {

  PersonalIdDTO personalId;
  String fullstandigtNamn;
  String forskrivarkod;
  List<CodeTypeDTO> befattning;
  EnhetDTO enhet;

  @Value
  @Builder
  public static class PersonalIdDTO {

    String root;
    String extension;
  }

  @Value
  @Builder
  public static class EnhetDTO {

    HsaIdDTO enhetsId;
    ArbetsplatskodDTO arbetsplatskod;
    String enhetsnamn;
    String postadress;
    String postnummer;
    String postort;
    String telefonnummer;
    String epost;
    VardgivareDTO vardgivare;

    @Value
    @Builder
    public static class ArbetsplatskodDTO {

      String root;
      String extension;
    }

    @Value
    @Builder
    public static class VardgivareDTO {

      HsaIdDTO vardgivareId;
      String vardgivarnamn;
    }

    @Value
    @Builder
    public static class HsaIdDTO {

      String root;
      String extension;
    }
  }
}
