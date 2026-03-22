package se.inera.intyg.intygmockservice.application.navigation.logentry;

import java.time.LocalDateTime;

public record LogEntryResponse(
    String logId,
    String systemId,
    String systemName,
    String activityType,
    String certificateId,
    String purpose,
    LocalDateTime activityStart,
    String userId,
    String userAssignment,
    String careUnitId,
    String careProviderName) {}
