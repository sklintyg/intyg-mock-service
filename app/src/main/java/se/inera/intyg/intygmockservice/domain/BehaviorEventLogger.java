package se.inera.intyg.intygmockservice.domain;

public interface BehaviorEventLogger {
  void logDelayApplied(ServiceName serviceName, String certificateId, BehaviorRule rule);

  void logErrorSkipped(ServiceName serviceName, String certificateId, BehaviorRule rule);
}
