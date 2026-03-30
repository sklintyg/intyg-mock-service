package se.inera.intyg.intygmockservice.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.inera.intyg.intygmockservice.application.registercertificate.api.RegisterCertificateResponderImpl;
import se.inera.intyg.intygmockservice.application.registercertificate.service.RegisterCertificateService;
import se.inera.intyg.intygmockservice.application.revokecertificate.api.RevokeCertificateResponderImpl;
import se.inera.intyg.intygmockservice.application.revokecertificate.service.RevokeCertificateService;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.api.SendMessageToRecipientResponderImpl;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.service.SendMessageToRecipientService;
import se.inera.intyg.intygmockservice.application.statusupdates.api.CertificateStatusUpdateForCareResponderImpl;
import se.inera.intyg.intygmockservice.application.statusupdates.service.CertificateStatusUpdateForCareService;
import se.inera.intyg.intygmockservice.application.storelog.api.StoreLogResponderImpl;
import se.inera.intyg.intygmockservice.application.storelog.service.StoreLogService;

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
  public EndpointImpl registerCertificateEndpoint(RegisterCertificateService service) {
    final var endpoint = new EndpointImpl(bus, new RegisterCertificateResponderImpl(service));

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
  public EndpointImpl storeLogEndpoint(StoreLogService storeLogService) {
    final var endpoint = new EndpointImpl(bus, new StoreLogResponderImpl(storeLogService));
    endpoint.publish("/informationsecurity/auditing/log/StoreLog/v2/rivtabp21");
    return endpoint;
  }
}
