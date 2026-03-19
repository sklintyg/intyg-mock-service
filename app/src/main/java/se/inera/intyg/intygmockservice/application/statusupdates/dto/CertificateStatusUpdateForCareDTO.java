package se.inera.intyg.intygmockservice.application.statusupdates.dto;

import lombok.Builder;
import lombok.Value;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO;

@Value
@Builder
public class CertificateStatusUpdateForCareDTO {

  IntygDTO intyg;
  Handelse handelse;
  Fragor skickadeFragor;
  Fragor mottagnaFragor;
  HanteratAv hanteratAv;

  @Value
  @Builder
  public static class Handelse {

    Handelsekod handelsekod;
    String tidpunkt;

    @Value
    @Builder
    public static class Handelsekod {

      String code;
      String codeSystem;
      String displayName;
    }
  }

  @Value
  @Builder
  public static class Fragor {

    int totalt;
    int ejBesvarade;
    int besvarade;
    int hanterade;
  }

  @Value
  @Builder
  public static class HanteratAv {

    String root;
    String extension;
  }
}
