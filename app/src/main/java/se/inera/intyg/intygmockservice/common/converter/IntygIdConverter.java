package se.inera.intyg.intygmockservice.common.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.IntygId;

@Component
public class IntygIdConverter {

    public IntygDTO.IntygsId convert(IntygId source) {
        final var target = new IntygDTO.IntygsId();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }
}
