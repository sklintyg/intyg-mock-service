package se.inera.intyg.intygmockservice.soap.api;

import jakarta.jws.WebService;
import jakarta.xml.bind.JAXBContext;
import java.io.StringWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.CertificateStatusUpdateForCareRepository;
import se.inera.intyg.intygmockservice.dto.converter.CertificateStatusUpdateForCareConverter;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponseType;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.ObjectFactory;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.DatePeriodType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@WebService
@Component
@Slf4j
@RequiredArgsConstructor
public class CertificateStatusUpdateForCare implements CertificateStatusUpdateForCareResponderInterface {

    private final CertificateStatusUpdateForCareConverter converter;
    private final CertificateStatusUpdateForCareRepository repository;

    @Override
    public CertificateStatusUpdateForCareResponseType certificateStatusUpdateForCare(String logicalAdress,
        CertificateStatusUpdateForCareType certificateStatusUpdateForCareType) {

        repository.add(logicalAdress, certificateStatusUpdateForCareType);

        log(certificateStatusUpdateForCareType);

        final var response = new CertificateStatusUpdateForCareResponseType();
        final var result = new ResultType();
        response.setResult(result);
        result.setResultCode(ResultCodeType.OK);

        return response;
    }

    private void log(CertificateStatusUpdateForCareType certificateStatusUpdateForCare) {
        final var factory = new ObjectFactory();
        final var element = factory.createCertificateStatusUpdateForCare(certificateStatusUpdateForCare);
        try {
            final var context = JAXBContext.newInstance(
                CertificateStatusUpdateForCareResponseType.class,
                DatePeriodType.class
            );
            final var writer = new StringWriter();
            context.createMarshaller().marshal(element, writer);
            log.info(writer.toString());
            log.info(converter.convert(certificateStatusUpdateForCare).toString());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
