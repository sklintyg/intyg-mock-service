package se.inera.intyg.intygmockservice.sendmessagetorecipient.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.converter.HoSPersonalConverter;
import se.inera.intyg.intygmockservice.common.dto.CodeTypeDTO;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.common.dto.PatientDTO.PersonId;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.dto.SendMessageToRecipientDTO;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;

@Component
@RequiredArgsConstructor
public class SendMessageToRecipientConverter {

  private final HoSPersonalConverter hoSPersonalConverter;

  public SendMessageToRecipientDTO convert(SendMessageToRecipientType source) {
    return SendMessageToRecipientDTO.builder()
        .logiskAdressMottagare(source.getLogiskAdressMottagare())
        .meddelandeId(source.getMeddelandeId())
        .skickatTidpunkt(source.getSkickatTidpunkt())
        .intygsId(
            IntygsId.builder()
                .root(source.getIntygsId().getRoot())
                .extension(source.getIntygsId().getExtension())
                .build())
        .patientPersonId(
            PersonId.builder()
                .root(source.getPatientPersonId().getRoot())
                .extension(source.getPatientPersonId().getExtension())
                .build())
        .amne(
            CodeTypeDTO.builder()
                .code(source.getAmne().getCode())
                .codeSystem(source.getAmne().getCodeSystem())
                .displayName(source.getAmne().getDisplayName())
                .build())
        .rubrik(source.getRubrik())
        .meddelande(source.getMeddelande())
        .skickatAv(hoSPersonalConverter.convert(source.getSkickatAv()))
        .build();
  }
}
