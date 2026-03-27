package se.inera.intyg.intygmockservice.infrastructure.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.domain.behavior.model.RuleEvaluation;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;

@Component
@Slf4j
public class BehaviorLogger implements BehaviorEventLogger {

  public void logErrorSkipped(RuleEvaluation evaluation) {
    final var result = evaluation.errorResult().orElseThrow();
    log.atInfo()
        .setMessage(
            "%s with certificateId '%s' not stored due to simulated %s behavior (rule %s)"
                .formatted(
                    toDisplayName(evaluation.serviceName()),
                    evaluation.certificateId(),
                    result.getErrorId() != null ? result.getErrorId() : result.getResultCode(),
                    evaluation.ruleId()))
        .addKeyValue("behavior.rule.id", evaluation.ruleId())
        .addKeyValue("behavior.rule.result_code", result.getResultCode())
        .addKeyValue("behavior.rule.error_id", result.getErrorId())
        .addKeyValue("event.service", evaluation.serviceName().name())
        .addKeyValue("event.certificate.id", evaluation.certificateId())
        .log();
  }

  public void logDelayApplied(RuleEvaluation evaluation) {
    log.atInfo()
        .setMessage(
            "%s with certificateId '%s' delayed %d ms by behavior rule (rule %s)"
                .formatted(
                    toDisplayName(evaluation.serviceName()),
                    evaluation.certificateId(),
                    evaluation.delayMillis(),
                    evaluation.ruleId()))
        .addKeyValue("behavior.rule.id", evaluation.ruleId())
        .addKeyValue("behavior.rule.delay_millis", evaluation.delayMillis())
        .addKeyValue("event.service", evaluation.serviceName().name())
        .addKeyValue("event.certificate.id", evaluation.certificateId())
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
