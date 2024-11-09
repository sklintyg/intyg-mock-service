package se.inera.intyg.intygmockservice.registercertificate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.registercertificate.converter.RegisterCertificateConverter;
import se.inera.intyg.intygmockservice.registercertificate.repository.RegisterCertificateRepository;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterCertificateResponderImpl implements RegisterCertificateResponderInterface {

    private final RegisterCertificateRepository repository;
    private final RegisterCertificateConverter converter;

    @Override
    public RegisterCertificateResponseType registerCertificate(String logicalAddress, RegisterCertificateType registerCertificateType) {
        repository.add(logicalAddress, registerCertificateType);

        final var response = new RegisterCertificateResponseType();
        final var result = new ResultType();
        response.setResult(result);
        result.setResultCode(ResultCodeType.OK);

        final var registerCertificateDTO = converter.convert(registerCertificateType);

        log.atInfo().setMessage(
                "Register certificate '%s' received".formatted(
                    registerCertificateDTO.getIntyg().getIntygsId().getExtension()
                )
            )
            .addKeyValue("event.logical_address", logicalAddress)
            .addKeyValue("event.certificate.id",
                registerCertificateDTO.getIntyg().getIntygsId().getExtension()
            )
            .addKeyValue("event.answered.message.id", registerCertificateDTO.getSvarPa() != null
                ? registerCertificateDTO.getSvarPa().getMeddelandeId()
                : "-"
            )
            .log();

        return response;
    }
}
