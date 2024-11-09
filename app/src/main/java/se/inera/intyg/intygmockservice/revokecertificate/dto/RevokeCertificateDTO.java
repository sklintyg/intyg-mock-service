package se.inera.intyg.intygmockservice.revokecertificate.dto;

import java.time.LocalDateTime;
import lombok.Data;
import se.inera.intyg.intygmockservice.common.dto.HoSPersonalDTO;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.common.dto.PatientDTO.PersonId;

@Data
public class RevokeCertificateDTO {

    private IntygsId intygsId;
    private HoSPersonalDTO skickadAv;
    private PersonId patientPersonId;
    private LocalDateTime skickatTidpunkt;
    private String meddelande;
}