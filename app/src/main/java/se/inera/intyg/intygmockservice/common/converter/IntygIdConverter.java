package se.inera.intyg.intygmockservice.common.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.IntygId;

@Component
public class IntygIdConverter {

  public IntygDTO.IntygsId convert(IntygId source) {
    return IntygDTO.IntygsId.builder()
        .root(source.getRoot())
        .extension(source.getExtension())
        .build();
  }
}
