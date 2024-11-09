package se.inera.intyg.intygmockservice.revokecertificate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.revokecertificate.converter.RevokeCertificateConverter;
import se.inera.intyg.intygmockservice.revokecertificate.repository.RevokeCertificateRepository;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Service
@Slf4j
public class RevokeCertificateResponderImpl implements RevokeCertificateResponderInterface {

    private final RevokeCertificateRepository repository;
    private final RevokeCertificateConverter converter;

    public RevokeCertificateResponderImpl(RevokeCertificateRepository repository, RevokeCertificateConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public RevokeCertificateResponseType revokeCertificate(String logicalAddress, RevokeCertificateType revokeCertificate) {
        repository.add(logicalAddress, revokeCertificate);

        final var response = new RevokeCertificateResponseType();
        final var result = new ResultType();
        response.setResult(result);
        result.setResultCode(ResultCodeType.OK);

        final var dto = converter.convert(revokeCertificate);

        log.atInfo().setMessage(
                "Certificate '%s' revoked with message '%s'".formatted(
                    dto.getIntygsId().getExtension(),
                    dto.getMeddelande()
                )
            )
            .addKeyValue("event.logical_address", logicalAddress)
            .addKeyValue("event.certificate.id", dto.getIntygsId().getExtension())
            .log();

        return response;
    }
}