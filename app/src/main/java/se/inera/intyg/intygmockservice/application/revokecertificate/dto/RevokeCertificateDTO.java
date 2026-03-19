package se.inera.intyg.intygmockservice.application.revokecertificate.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.intygmockservice.application.common.dto.HoSPersonalDTO;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.application.common.dto.PatientDTO.PersonId;

@Value
@Builder
public class RevokeCertificateDTO {

  IntygsId intygsId;
  HoSPersonalDTO skickadAv;
  PersonId patientPersonId;
  LocalDateTime skickatTidpunkt;
  String meddelande;
}
