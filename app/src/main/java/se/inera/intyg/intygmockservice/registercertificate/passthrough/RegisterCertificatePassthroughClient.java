package se.inera.intyg.intygmockservice.registercertificate.passthrough;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.config.passthrough.PassthroughClientFactory;
import se.inera.intyg.intygmockservice.config.properties.PassthroughProperties;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Component
@Slf4j
public class RegisterCertificatePassthroughClient {

  private final String upstreamUrl;
  private final RegisterCertificateResponderInterface proxy;

  public RegisterCertificatePassthroughClient(
      PassthroughProperties props, PassthroughClientFactory factory) {
    final var config = props.registerCertificate();
    if (config != null && config.enabled()) {
      this.upstreamUrl = config.url();
      this.proxy = factory.createClient(RegisterCertificateResponderInterface.class, config.url());
      log.atInfo()
          .setMessage("RegisterCertificate passthrough enabled")
          .addKeyValue("url", config.url())
          .log();
    } else {
      this.upstreamUrl = null;
      this.proxy = null;
    }
  }

  public Optional<RegisterCertificateResponseType> forward(
      String logicalAddress, RegisterCertificateType request) {
    if (proxy == null) {
      return Optional.empty();
    }
    try {
      final var response = proxy.registerCertificate(logicalAddress, request);
      log.atInfo()
          .setMessage(
              "RegisterCertificate forwarded to upstream with logicalAddress: " + logicalAddress)
          .addKeyValue("upstream.url", upstreamUrl)
          .addKeyValue("upstream.result", response.getResult().getResultCode().name())
          .log();
      return Optional.of(response);
    } catch (Exception e) {
      log.atError()
          .setMessage(
              "RegisterCertificate passthrough failed with logic address: " + logicalAddress)
          .addKeyValue("upstream.url", upstreamUrl)
          .setCause(e)
          .log();
      final var error = new RegisterCertificateResponseType();
      final var result = new ResultType();
      result.setResultCode(ResultCodeType.ERROR);
      result.setResultText(e.getMessage());
      error.setResult(result);
      return Optional.of(error);
    }
  }
}
