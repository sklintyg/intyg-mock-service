package se.inera.intyg.intygmockservice.application.registercertificate.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.application.common.converter.IntygConverter;
import se.inera.intyg.intygmockservice.application.common.dto.MeddelandeReferensDTO;
import se.inera.intyg.intygmockservice.application.registercertificate.dto.RegisterCertificateDTO;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.MeddelandeReferens;

@Component
@RequiredArgsConstructor
public class RegisterCertificateConverter {

  private final IntygConverter intygConverter;

  public RegisterCertificateDTO convert(RegisterCertificateType source) {
    return RegisterCertificateDTO.builder()
        .intyg(intygConverter.convert(source.getIntyg()))
        .svarPa(source.getSvarPa() != null ? convertMeddelandeReferens(source.getSvarPa()) : null)
        .build();
  }

  private MeddelandeReferensDTO convertMeddelandeReferens(MeddelandeReferens svarPa) {
    return MeddelandeReferensDTO.builder()
        .meddelandeId(svarPa.getMeddelandeId())
        .referensId(svarPa.getReferensId())
        .build();
  }
}
