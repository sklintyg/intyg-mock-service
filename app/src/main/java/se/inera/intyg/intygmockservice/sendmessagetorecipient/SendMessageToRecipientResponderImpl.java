package se.inera.intyg.intygmockservice.sendmessagetorecipient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.converter.SendMessageToRecipientConverter;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.repository.SendMessageToRecipientRepository;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientResponseType;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Service
@Slf4j
public class SendMessageToRecipientResponderImpl implements SendMessageToRecipientResponderInterface {

    private final SendMessageToRecipientRepository repository;
    private final SendMessageToRecipientConverter converter;

    public SendMessageToRecipientResponderImpl(SendMessageToRecipientRepository repository, SendMessageToRecipientConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public SendMessageToRecipientResponseType sendMessageToRecipient(String logicalAddress,
        SendMessageToRecipientType sendMessageToRecipient) {
        repository.add(logicalAddress, sendMessageToRecipient);

        final var response = new SendMessageToRecipientResponseType();
        final var result = new ResultType();
        response.setResult(result);
        result.setResultCode(ResultCodeType.OK);

        final var sendMessageToRecipientDTO = converter.convert(sendMessageToRecipient);

        log.atInfo().setMessage(
                "Message '%s' on certificate '%s' sent to '%s' with content '%s'".formatted(
                    sendMessageToRecipientDTO.getMeddelandeId(),
                    sendMessageToRecipientDTO.getIntygsId().getExtension(),
                    logicalAddress,
                    sendMessageToRecipientDTO.getMeddelande()
                )
            )
            .addKeyValue("event.logical_address", logicalAddress)
            .addKeyValue("event.certificate.id", sendMessageToRecipientDTO.getIntygsId().getExtension())
            .addKeyValue("event.message.id", sendMessageToRecipientDTO.getMeddelandeId())
            .log();

        return response;
    }
}