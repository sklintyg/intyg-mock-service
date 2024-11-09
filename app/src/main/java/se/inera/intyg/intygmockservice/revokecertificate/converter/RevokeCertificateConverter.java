package se.inera.intyg.intygmockservice.revokecertificate.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.converter.HoSPersonalConverter;
import se.inera.intyg.intygmockservice.common.converter.IntygIdConverter;
import se.inera.intyg.intygmockservice.common.converter.PersonIdConverter;
import se.inera.intyg.intygmockservice.revokecertificate.dto.RevokeCertificateDTO;
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
        RevokeCertificateDTO target = new RevokeCertificateDTO();
        target.setIntygsId(intygIdConverter.convert(source.getIntygsId()));
        target.setSkickatTidpunkt(source.getSkickatTidpunkt());
        target.setSkickadAv(hoSPersonalConverter.convert(source.getSkickatAv()));
        target.setMeddelande(source.getMeddelande());
        target.setPatientPersonId(personIdConverter.convert(source.getPatientPersonId()));
        return target;
    }
}