package se.inera.intyg.intygmockservice.infrastructure.passthrough;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.infrastructure.config.passthrough.PassthroughClientFactory;
import se.inera.intyg.intygmockservice.infrastructure.config.properties.PassthroughProperties;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientResponseType;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Component
@Slf4j
public class SendMessageToRecipientPassthroughClient {

  private final String upstreamUrl;
  private final SendMessageToRecipientResponderInterface proxy;

  public SendMessageToRecipientPassthroughClient(
      PassthroughProperties props, PassthroughClientFactory factory) {
    final var config = props.sendMessageToRecipient();
    if (config != null && config.enabled()) {
      this.upstreamUrl = config.url();
      this.proxy =
          factory.createClient(SendMessageToRecipientResponderInterface.class, config.url());
      log.atInfo()
          .setMessage("SendMessageToRecipient passthrough enabled")
          .addKeyValue("url", config.url())
          .log();
    } else {
      this.upstreamUrl = null;
      this.proxy = null;
    }
  }

  public Optional<SendMessageToRecipientResponseType> forward(
      String logicalAddress, SendMessageToRecipientType request) {
    if (proxy == null) {
      return Optional.empty();
    }
    try {
      final var response = proxy.sendMessageToRecipient(logicalAddress, request);
      log.atInfo()
          .setMessage(
              "SendMessageToRecipient forwarded to upstream with logicalAddress: " + logicalAddress)
          .addKeyValue("upstream.url", upstreamUrl)
          .addKeyValue("upstream.result", response.getResult().getResultCode().name())
          .log();
      return Optional.of(response);
    } catch (Exception e) {
      log.atError()
          .setMessage(
              "SendMessageToRecipient passthrough failed with logical address: " + logicalAddress)
          .addKeyValue("upstream.url", upstreamUrl)
          .setCause(e)
          .log();
      final var error = new SendMessageToRecipientResponseType();
      final var result = new ResultType();
      result.setResultCode(ResultCodeType.ERROR);
      result.setResultText(e.getMessage());
      error.setResult(result);
      return Optional.of(error);
    }
  }
}
