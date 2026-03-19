package se.inera.intyg.intygmockservice.application.registercertificate.dto;

import lombok.Builder;
import lombok.Value;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.application.common.dto.MeddelandeReferensDTO;

@Value
@Builder
public class RegisterCertificateDTO {

  IntygDTO intyg;
  MeddelandeReferensDTO svarPa;
}
