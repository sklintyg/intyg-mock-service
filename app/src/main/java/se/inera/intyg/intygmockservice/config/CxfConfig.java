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
import se.inera.intyg.intygmockservice.revokecertificate.RevokeCertificateService;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.SendMessageToRecipientResponderImpl;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.SendMessageToRecipientService;
import se.inera.intyg.intygmockservice.statusupdates.CertificateStatusUpdateForCareResponderImpl;
import se.inera.intyg.intygmockservice.statusupdates.CertificateStatusUpdateForCareService;
import se.inera.intyg.intygmockservice.storelog.StoreLogResponderImpl;
import se.inera.intyg.intygmockservice.storelog.converter.StoreLogTypeConverter;
import se.inera.intyg.intygmockservice.storelog.repository.StoreLogTypeRepository;

@Configuration
@RequiredArgsConstructor
public class CxfConfig {

  private final Bus bus;

  @Bean
  public EndpointImpl certificateStatusUpdateForCareEndpoint(
      CertificateStatusUpdateForCareService certificateStatusUpdateForCareService) {
    final var endpoint =
        new EndpointImpl(
            bus,
            new CertificateStatusUpdateForCareResponderImpl(certificateStatusUpdateForCareService));

    endpoint.publish(
        "/clinicalprocess/healthcond/certificate/CertificateStatusUpdateForCare/3/rivtabp21");
    return endpoint;
  }

  @Bean
  public EndpointImpl registerCertificateEndpoint(
      RegisterCertificateConverter converter, RegisterCertificateRepository repository) {
    final var endpoint =
        new EndpointImpl(bus, new RegisterCertificateResponderImpl(repository, converter));

    endpoint.publish("/clinicalprocess/healthcond/certificate/RegisterCertificate/3/rivtabp21");
    return endpoint;
  }

  @Bean
  public EndpointImpl revokeCertificateEndpoint(RevokeCertificateService revokeCertificateService) {
    final var endpoint =
        new EndpointImpl(bus, new RevokeCertificateResponderImpl(revokeCertificateService));

    endpoint.publish("/clinicalprocess/healthcond/certificate/RevokeCertificate/2/rivtabp21");
    return endpoint;
  }

  @Bean
  public EndpointImpl sendMessageToRecipientEndpoint(
      SendMessageToRecipientService sendMessageToRecipientService) {
    final var endpoint =
        new EndpointImpl(
            bus, new SendMessageToRecipientResponderImpl(sendMessageToRecipientService));

    endpoint.publish("/clinicalprocess/healthcond/certificate/SendMessageToRecipient/2/rivtabp21");
    return endpoint;
  }

  @Bean
  public EndpointImpl storeLogEndpoint(
      StoreLogTypeConverter converter, StoreLogTypeRepository repository) {
    final var endpoint = new EndpointImpl(bus, new StoreLogResponderImpl(converter, repository));
    endpoint.publish("/informationsecurity/auditing/log/StoreLog/v2/rivtabp21");
    return endpoint;
  }
}
