package se.inera.intyg.intygmockservice.application.behavior.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.domain.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.ServiceName;

@Component
@Slf4j
public class BehaviorLogger {

  public void logErrorSkipped(ServiceName serviceName, String certificateId, BehaviorRule rule) {
    log.atInfo()
        .setMessage(
            "%s with certificateId '%s' not stored due to simulated %s behavior (rule %s)"
                .formatted(
                    toDisplayName(serviceName),
                    certificateId,
                    rule.getErrorId() != null ? rule.getErrorId() : rule.getResultCode(),
                    rule.getId()))
        .addKeyValue("behavior.rule.id", rule.getId())
        .addKeyValue("behavior.rule.result_code", rule.getResultCode())
        .addKeyValue("behavior.rule.error_id", rule.getErrorId())
        .addKeyValue("event.service", serviceName.name())
        .addKeyValue("event.certificate.id", certificateId)
        .log();
  }

  public void logDelayApplied(ServiceName serviceName, String certificateId, BehaviorRule rule) {
    log.atInfo()
        .setMessage(
            "%s with certificateId '%s' delayed %d ms by behavior rule (rule %s)"
                .formatted(
                    toDisplayName(serviceName), certificateId, rule.getDelayMillis(), rule.getId()))
        .addKeyValue("behavior.rule.id", rule.getId())
        .addKeyValue("behavior.rule.delay_millis", rule.getDelayMillis())
        .addKeyValue("event.service", serviceName.name())
        .addKeyValue("event.certificate.id", certificateId)
        .log();
  }

  private String toDisplayName(ServiceName serviceName) {
    return switch (serviceName) {
      case REGISTER_CERTIFICATE -> "RegisterCertificate";
      case REVOKE_CERTIFICATE -> "RevokeCertificate";
      case SEND_MESSAGE_TO_RECIPIENT -> "SendMessageToRecipient";
      case CERTIFICATE_STATUS_UPDATE_FOR_CARE -> "CertificateStatusUpdateForCare";
      case STORE_LOG -> "StoreLog";
    };
  }
}
