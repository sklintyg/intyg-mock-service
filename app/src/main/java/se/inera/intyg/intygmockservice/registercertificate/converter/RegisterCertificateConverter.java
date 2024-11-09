package se.inera.intyg.intygmockservice.registercertificate.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.converter.IntygConverter;
import se.inera.intyg.intygmockservice.common.dto.MeddelandeReferensDTO;
import se.inera.intyg.intygmockservice.registercertificate.dto.RegisterCertificateDTO;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.MeddelandeReferens;

@Component
@RequiredArgsConstructor
public class RegisterCertificateConverter {

    private final IntygConverter intygConverter;

    public RegisterCertificateDTO convert(RegisterCertificateType source) {
        final var target = new RegisterCertificateDTO();

        target.setIntyg(intygConverter.convert(source.getIntyg()));
        if (source.getSvarPa() != null) {
            target.setSvarPa(convertMeddelandeReferens(source.getSvarPa()));
        }
        return target;
    }

    private MeddelandeReferensDTO convertMeddelandeReferens(MeddelandeReferens svarPa) {
        final var meddelandeReferensDTO = new MeddelandeReferensDTO();
        meddelandeReferensDTO.setMeddelandeId(svarPa.getMeddelandeId());
        meddelandeReferensDTO.setReferensId(svarPa.getReferensId());
        return meddelandeReferensDTO;
    }
}