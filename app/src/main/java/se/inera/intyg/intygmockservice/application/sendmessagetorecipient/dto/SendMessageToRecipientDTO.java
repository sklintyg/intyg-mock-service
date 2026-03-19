package se.inera.intyg.intygmockservice.application.sendmessagetorecipient.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.intygmockservice.application.common.dto.CodeTypeDTO;
import se.inera.intyg.intygmockservice.application.common.dto.HoSPersonalDTO;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.application.common.dto.PatientDTO.PersonId;

@Value
@Builder
public class SendMessageToRecipientDTO {

  String logiskAdressMottagare;
  String meddelandeId;
  LocalDateTime skickatTidpunkt;
  IntygsId intygsId;
  PersonId patientPersonId;
  CodeTypeDTO amne;
  String rubrik;
  String meddelande;
  HoSPersonalDTO skickatAv;
}
