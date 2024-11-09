package se.inera.intyg.intygmockservice.common.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.dto.PatientDTO;

@Component
public class PersonIdConverter {

    public PatientDTO.PersonId convert(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.PersonId source) {
        PatientDTO.PersonId target = new PatientDTO.PersonId();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }
}
