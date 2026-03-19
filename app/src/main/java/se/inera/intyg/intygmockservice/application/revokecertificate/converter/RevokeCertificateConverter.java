package se.inera.intyg.intygmockservice.application.revokecertificate.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.application.common.converter.HoSPersonalConverter;
import se.inera.intyg.intygmockservice.application.common.converter.IntygIdConverter;
import se.inera.intyg.intygmockservice.application.common.converter.PersonIdConverter;
import se.inera.intyg.intygmockservice.application.revokecertificate.dto.RevokeCertificateDTO;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;

@Component
@RequiredArgsConstructor
public class RevokeCertificateConverter {

  private final HoSPersonalConverter hoSPersonalConverter;
  private final IntygIdConverter intygIdConverter;
  private final PersonIdConverter personIdConverter;

  public RevokeCertificateDTO convert(RevokeCertificateType source) {
    if (source == null) {
      return null;
    }
    return RevokeCertificateDTO.builder()
        .intygsId(intygIdConverter.convert(source.getIntygsId()))
        .skickatTidpunkt(source.getSkickatTidpunkt())
        .skickadAv(hoSPersonalConverter.convert(source.getSkickatAv()))
        .meddelande(source.getMeddelande())
        .patientPersonId(personIdConverter.convert(source.getPatientPersonId()))
        .build();
  }
}
