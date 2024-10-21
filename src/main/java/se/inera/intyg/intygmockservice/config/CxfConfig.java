package se.inera.intyg.intygmockservice.config;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.inera.intyg.intygmockservice.CertificateStatusUpdateForCareRepository;
import se.inera.intyg.intygmockservice.dto.converter.CertificateStatusUpdateForCareConverter;
import se.inera.intyg.intygmockservice.soap.api.CertificateStatusUpdateForCare;

@Configuration
@RequiredArgsConstructor
public class CxfConfig {

    private final Bus bus;

    @Bean
    public EndpointImpl endpoint(CertificateStatusUpdateForCareConverter converter, CertificateStatusUpdateForCareRepository repository) {
        final var endpoint = new EndpointImpl(bus, new CertificateStatusUpdateForCare(converter, repository));
        endpoint.publish("/clinicalprocess/healthcond/certificate/CertificateStatusUpdateForCare/3/rivtabp21");
        return endpoint;
    }
}
