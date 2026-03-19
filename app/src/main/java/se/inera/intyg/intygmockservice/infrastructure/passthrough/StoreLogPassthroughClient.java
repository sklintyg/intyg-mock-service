package se.inera.intyg.intygmockservice.infrastructure.passthrough;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.infrastructure.config.passthrough.PassthroughClientFactory;
import se.inera.intyg.intygmockservice.infrastructure.config.properties.PassthroughProperties;
import se.riv.informationsecurity.auditing.log.StoreLog.v2.rivtabp21.StoreLogResponderInterface;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogResponseType;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;
import se.riv.informationsecurity.auditing.log.v2.ResultCodeType;
import se.riv.informationsecurity.auditing.log.v2.ResultType;

@Component
@Slf4j
public class StoreLogPassthroughClient {

  private final String upstreamUrl;
  private final StoreLogResponderInterface proxy;

  public StoreLogPassthroughClient(PassthroughProperties props, PassthroughClientFactory factory) {
    final var config = props.storeLog();
    if (config != null && config.enabled()) {
      this.upstreamUrl = config.url();
      this.proxy = factory.createClient(StoreLogResponderInterface.class, config.url());
      log.atInfo()
          .setMessage("StoreLog passthrough enabled")
          .addKeyValue("url", config.url())
          .log();
    } else {
      this.upstreamUrl = null;
      this.proxy = null;
    }
  }

  public Optional<StoreLogResponseType> forward(String logicalAddress, StoreLogType request) {
    if (proxy == null) {
      return Optional.empty();
    }
    try {
      final var response = proxy.storeLog(logicalAddress, request);
      log.atInfo()
          .setMessage("StoreLog forwarded to upstream with logicalAddress: " + logicalAddress)
          .addKeyValue("upstream.url", upstreamUrl)
          .addKeyValue("upstream.result", response.getResult().getResultCode().name())
          .log();
      return Optional.of(response);
    } catch (Exception e) {
      log.atError()
          .setMessage("StoreLog passthrough failed with logical address: " + logicalAddress)
          .addKeyValue("upstream.url", upstreamUrl)
          .setCause(e)
          .log();
      final var error = new StoreLogResponseType();
      final var result = new ResultType();
      result.setResultCode(ResultCodeType.ERROR);
      result.setResultText(e.getMessage());
      error.setResult(result);
      return Optional.of(error);
    }
  }
}
