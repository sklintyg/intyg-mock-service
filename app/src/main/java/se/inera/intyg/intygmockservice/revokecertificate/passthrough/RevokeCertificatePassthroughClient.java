package se.inera.intyg.intygmockservice.revokecertificate.passthrough;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.config.passthrough.PassthroughClientFactory;
import se.inera.intyg.intygmockservice.config.properties.PassthroughProperties;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Component
@Slf4j
public class RevokeCertificatePassthroughClient {

  private final String upstreamUrl;
  private final RevokeCertificateResponderInterface proxy;

  public RevokeCertificatePassthroughClient(
      PassthroughProperties props, PassthroughClientFactory factory) {
    final var config = props.revokeCertificate();
    if (config != null && config.enabled()) {
      this.upstreamUrl = config.url();
      this.proxy = factory.createClient(RevokeCertificateResponderInterface.class, config.url());
      log.atInfo()
          .setMessage("RevokeCertificate passthrough enabled")
          .addKeyValue("url", config.url())
          .log();
    } else {
      this.upstreamUrl = null;
      this.proxy = null;
    }
  }

  public Optional<RevokeCertificateResponseType> forward(
      String logicalAddress, RevokeCertificateType request) {
    if (proxy == null) {
      return Optional.empty();
    }
    try {
      final var response = proxy.revokeCertificate(logicalAddress, request);
      log.atInfo()
          .setMessage(
              "RevokeCertificate forwarded to upstream with logicalAddress: " + logicalAddress)
          .addKeyValue("upstream.url", upstreamUrl)
          .addKeyValue("upstream.result", response.getResult().getResultCode().name())
          .log();
      return Optional.of(response);
    } catch (Exception e) {
      log.atError()
          .setMessage(
              "RevokeCertificate passthrough failed with logical address: " + logicalAddress)
          .addKeyValue("upstream.url", upstreamUrl)
          .setCause(e)
          .log();
      final var error = new RevokeCertificateResponseType();
      final var result = new ResultType();
      result.setResultCode(ResultCodeType.ERROR);
      result.setResultText(e.getMessage());
      error.setResult(result);
      return Optional.of(error);
    }
  }
}
