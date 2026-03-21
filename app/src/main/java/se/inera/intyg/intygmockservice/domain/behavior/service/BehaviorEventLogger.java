package se.inera.intyg.intygmockservice.domain.behavior.service;

import se.inera.intyg.intygmockservice.domain.behavior.model.BehaviorRule;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;

public interface BehaviorEventLogger {
  void logDelayApplied(ServiceName serviceName, String certificateId, BehaviorRule rule);

  void logErrorSkipped(ServiceName serviceName, String certificateId, BehaviorRule rule);
}
