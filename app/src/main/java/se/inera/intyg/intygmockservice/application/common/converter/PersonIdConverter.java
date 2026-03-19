package se.inera.intyg.intygmockservice.common.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.dto.PatientDTO;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.PersonId;

@Component
public class PersonIdConverter {

  public PatientDTO.PersonId convert(PersonId source) {
    return PatientDTO.PersonId.builder()
        .root(source.getRoot())
        .extension(source.getExtension())
        .build();
  }
}
