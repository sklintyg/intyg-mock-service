package se.inera.intyg.intygmockservice.registercertificate.dto;

import lombok.Data;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.common.dto.MeddelandeReferensDTO;

@Data
public class RegisterCertificateDTO {

    private IntygDTO intyg;
    private MeddelandeReferensDTO svarPa;

}