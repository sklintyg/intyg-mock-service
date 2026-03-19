package se.inera.intyg.intygmockservice.infrastructure.config.passthrough;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.cxf.BusFactory;
import org.junit.jupiter.api.Test;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponderInterface;

class PassthroughClientFactoryTest {

  @Test
  void shouldCreateProxyForServiceInterface() {
    final var bus = BusFactory.getDefaultBus();
    final var factory = new PassthroughClientFactory(bus);

    final var proxy =
        factory.createClient(
            RegisterCertificateResponderInterface.class, "http://localhost:9999/test");

    assertNotNull(proxy);
    assertInstanceOf(RegisterCertificateResponderInterface.class, proxy);
  }
}
