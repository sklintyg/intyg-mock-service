package se.inera.intyg.intygmockservice.revokecertificate.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.intygmockservice.common.dto.HoSPersonalDTO;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.common.dto.PatientDTO.PersonId;

@Value
@Builder
public class RevokeCertificateDTO {

  IntygsId intygsId;
  HoSPersonalDTO skickadAv;
  PersonId patientPersonId;
  LocalDateTime skickatTidpunkt;
  String meddelande;
}
