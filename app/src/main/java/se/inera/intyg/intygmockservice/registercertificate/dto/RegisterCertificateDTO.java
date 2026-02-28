package se.inera.intyg.intygmockservice.registercertificate.dto;

import lombok.Builder;
import lombok.Value;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.common.dto.MeddelandeReferensDTO;

@Value
@Builder
public class RegisterCertificateDTO {

  IntygDTO intyg;
  MeddelandeReferensDTO svarPa;
}
