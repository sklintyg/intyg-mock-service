package se.inera.intyg.intygmockservice.config.passthrough;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassthroughClientFactory {

  private final Bus bus;

  public <T> T createClient(Class<T> serviceInterface, String url) {
    final var factory = new JaxWsProxyFactoryBean();
    factory.setBus(bus);
    factory.setServiceClass(serviceInterface);
    factory.setAddress(url);
    return serviceInterface.cast(factory.create());
  }
}
