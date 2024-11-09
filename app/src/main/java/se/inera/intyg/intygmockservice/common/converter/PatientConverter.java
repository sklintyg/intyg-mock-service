package se.inera.intyg.intygmockservice.common.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.dto.PatientDTO;

@Component
@RequiredArgsConstructor
public class PatientConverter {

    private final PersonIdConverter personIdConverter;

    public PatientDTO convert(
        se.riv.clinicalprocess.healthcond.certificate.v3.Patient source) {
        PatientDTO target = new PatientDTO();
        target.setPersonId(personIdConverter.convert(source.getPersonId()));
        target.setFornamn(source.getFornamn());
        target.setEfternamn(source.getEfternamn());
        target.setPostadress(source.getPostadress());
        target.setPostnummer(source.getPostnummer());
        target.setPostort(source.getPostort());
        return target;
    }

}
