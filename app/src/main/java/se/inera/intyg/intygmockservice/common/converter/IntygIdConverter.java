package se.inera.intyg.intygmockservice.common.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO;

@Component
public class IntygIdConverter {

    public IntygDTO.IntygsId convert(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.IntygId source) {
        IntygDTO.IntygsId target = new IntygDTO.IntygsId();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }
}
