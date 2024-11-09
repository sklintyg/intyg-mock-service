package se.inera.intyg.intygmockservice.sendmessagetorecipient.dto;

import java.time.LocalDateTime;
import lombok.Data;
import se.inera.intyg.intygmockservice.common.dto.CodeType;
import se.inera.intyg.intygmockservice.common.dto.HoSPersonalDTO;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.common.dto.PatientDTO.PersonId;

@Data
public class SendMessageToRecipientDTO {

    private String logiskAdressMottagare;
    private String meddelandeId;
    private LocalDateTime skickatTidpunkt;
    private IntygsId intygsId;
    private PersonId patientPersonId;
    private CodeType amne;
    private String rubrik;
    private String meddelande;
    private HoSPersonalDTO skickatAv;
}