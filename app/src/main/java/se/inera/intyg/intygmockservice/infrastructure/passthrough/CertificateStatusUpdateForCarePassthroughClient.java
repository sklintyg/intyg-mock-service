package se.inera.intyg.intygmockservice.infrastructure.passthrough;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.infrastructure.config.passthrough.PassthroughClientFactory;
import se.inera.intyg.intygmockservice.infrastructure.config.properties.PassthroughProperties;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponseType;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Component
@Slf4j
public class CertificateStatusUpdateForCarePassthroughClient {

  private final String upstreamUrl;
  private final CertificateStatusUpdateForCareResponderInterface proxy;

  public CertificateStatusUpdateForCarePassthroughClient(
      PassthroughProperties props, PassthroughClientFactory factory) {
    final var config = props.certificateStatusUpdateForCare();
    if (config != null && config.enabled()) {
      this.upstreamUrl = config.url();
      this.proxy =
          factory.createClient(
              CertificateStatusUpdateForCareResponderInterface.class, config.url());
      log.atInfo()
          .setMessage("CertificateStatusUpdateForCare passthrough enabled")
          .addKeyValue("url", config.url())
          .log();
    } else {
      this.upstreamUrl = null;
      this.proxy = null;
    }
  }

  public Optional<CertificateStatusUpdateForCareResponseType> forward(
      String logicalAddress, CertificateStatusUpdateForCareType request) {
    if (proxy == null) {
      return Optional.empty();
    }
    try {
      final var response = proxy.certificateStatusUpdateForCare(logicalAddress, request);
      log.atInfo()
          .setMessage(
              "CertificateStatusUpdateForCare forwarded to upstream with logicalAddress: "
                  + logicalAddress)
          .addKeyValue("upstream.url", upstreamUrl)
          .addKeyValue("upstream.result", response.getResult().getResultCode().name())
          .log();
      return Optional.of(response);
    } catch (Exception e) {
      log.atError()
          .setMessage(
              "CertificateStatusUpdateForCare passthrough failed with logical address: "
                  + logicalAddress)
          .addKeyValue("upstream.url", upstreamUrl)
          .setCause(e)
          .log();
      final var error = new CertificateStatusUpdateForCareResponseType();
      final var result = new ResultType();
      result.setResultCode(ResultCodeType.ERROR);
      result.setResultText(e.getMessage());
      error.setResult(result);
      return Optional.of(error);
    }
  }
}
