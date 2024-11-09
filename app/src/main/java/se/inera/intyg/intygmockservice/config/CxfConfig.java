package se.inera.intyg.intygmockservice.config;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.inera.intyg.intygmockservice.registercertificate.RegisterCertificateResponderImpl;
import se.inera.intyg.intygmockservice.registercertificate.converter.RegisterCertificateConverter;
import se.inera.intyg.intygmockservice.registercertificate.repository.RegisterCertificateRepository;
import se.inera.intyg.intygmockservice.revokecertificate.RevokeCertificateResponderImpl;
import se.inera.intyg.intygmockservice.revokecertificate.converter.RevokeCertificateConverter;
import se.inera.intyg.intygmockservice.revokecertificate.repository.RevokeCertificateRepository;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.SendMessageToRecipientResponderImpl;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.converter.SendMessageToRecipientConverter;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.repository.SendMessageToRecipientRepository;
import se.inera.intyg.intygmockservice.statusupdates.CertificateStatusUpdateForCareResponderImpl;
import se.inera.intyg.intygmockservice.statusupdates.converter.CertificateStatusUpdateForCareConverter;
import se.inera.intyg.intygmockservice.statusupdates.repository.CertificateStatusUpdateForCareRepository;

@Configuration
@RequiredArgsConstructor
public class CxfConfig {

    private final Bus bus;

    @Bean
    public EndpointImpl certificateStatusUpdateForCareEndpoint(CertificateStatusUpdateForCareConverter converter,
        CertificateStatusUpdateForCareRepository repository) {
        final var endpoint = new EndpointImpl(bus,
            new CertificateStatusUpdateForCareResponderImpl(repository, converter)
        );

        endpoint.publish(
            "/clinicalprocess/healthcond/certificate/CertificateStatusUpdateForCare/3/rivtabp21"
        );
        return endpoint;
    }

    @Bean
    public EndpointImpl registerCertificateEndpoint(RegisterCertificateConverter converter,
        RegisterCertificateRepository repository) {
        final var endpoint = new EndpointImpl(bus,
            new RegisterCertificateResponderImpl(repository, converter)
        );

        endpoint.publish(
            "/clinicalprocess/healthcond/certificate/RegisterCertificate/3/rivtabp21"
        );
        return endpoint;
    }

    @Bean
    public EndpointImpl revokeCertificateEndpoint(RevokeCertificateConverter converter,
        RevokeCertificateRepository repository) {
        final var endpoint = new EndpointImpl(bus,
            new RevokeCertificateResponderImpl(repository, converter)
        );

        endpoint.publish(
            "/clinicalprocess/healthcond/certificate/RevokeCertificate/2/rivtabp21"
        );
        return endpoint;
    }

    @Bean
    public EndpointImpl sendMessageToRecipientEndpoint(SendMessageToRecipientConverter converter,
        SendMessageToRecipientRepository repository) {
        final var endpoint = new EndpointImpl(bus,
            new SendMessageToRecipientResponderImpl(repository, converter)
        );

        endpoint.publish(
            "/clinicalprocess/healthcond/certificate/SendMessageToRecipient/2/rivtabp21"
        );
        return endpoint;
    }

}
