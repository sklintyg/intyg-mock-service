package se.inera.intyg.intygmockservice.sendmessagetorecipient.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.converter.HoSPersonalConverter;
import se.inera.intyg.intygmockservice.common.dto.CodeType;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.common.dto.PatientDTO.PersonId;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.dto.SendMessageToRecipientDTO;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;

@Component
@RequiredArgsConstructor
public class SendMessageToRecipientConverter {

    private final HoSPersonalConverter hoSPersonalConverter;

    public SendMessageToRecipientDTO convert(SendMessageToRecipientType source) {
        final var dto = new SendMessageToRecipientDTO();
        dto.setLogiskAdressMottagare(source.getLogiskAdressMottagare());
        dto.setMeddelandeId(source.getMeddelandeId());
        dto.setSkickatTidpunkt(source.getSkickatTidpunkt());

        final var intygsId = new IntygsId();
        intygsId.setRoot(source.getIntygsId().getRoot());
        intygsId.setExtension(source.getIntygsId().getExtension());
        dto.setIntygsId(intygsId);

        final var patientPersonId = new PersonId();
        patientPersonId.setRoot(source.getPatientPersonId().getRoot());
        patientPersonId.setExtension(source.getPatientPersonId().getExtension());
        dto.setPatientPersonId(patientPersonId);

        final var amne = new CodeType();
        amne.setCode(source.getAmne().getCode());
        amne.setCodeSystem(source.getAmne().getCodeSystem());
        amne.setDisplayName(source.getAmne().getDisplayName());
        dto.setAmne(amne);

        dto.setRubrik(source.getRubrik());
        dto.setMeddelande(source.getMeddelande());

        final var skickatAv = hoSPersonalConverter.convert(source.getSkickatAv());
        dto.setSkickatAv(skickatAv);

        return dto;
    }
}