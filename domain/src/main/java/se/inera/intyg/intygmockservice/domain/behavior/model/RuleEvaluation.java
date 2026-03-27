package se.inera.intyg.intygmockservice.domain.behavior.model;

import java.util.Optional;
import java.util.UUID;

public record RuleEvaluation(
    UUID ruleId,
    ServiceName serviceName,
    String certificateId,
    Optional<MockResponse> errorResult,
    boolean delayRequested,
    Long delayMillis,
    boolean exhausted) {}
