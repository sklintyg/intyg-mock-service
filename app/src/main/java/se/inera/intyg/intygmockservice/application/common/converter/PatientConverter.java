package se.inera.intyg.intygmockservice.application.common.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.application.common.dto.PatientDTO;
import se.riv.clinicalprocess.healthcond.certificate.v3.Patient;

@Component
@RequiredArgsConstructor
public class PatientConverter {

  private final PersonIdConverter personIdConverter;

  public PatientDTO convert(Patient source) {
    return PatientDTO.builder()
        .personId(personIdConverter.convert(source.getPersonId()))
        .fornamn(source.getFornamn())
        .efternamn(source.getEfternamn())
        .postadress(source.getPostadress())
        .postnummer(source.getPostnummer())
        .postort(source.getPostort())
        .build();
  }
}
