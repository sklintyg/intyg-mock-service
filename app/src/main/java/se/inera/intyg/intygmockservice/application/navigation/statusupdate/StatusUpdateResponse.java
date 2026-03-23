package se.inera.intyg.intygmockservice.application.navigation.statusupdate;

import java.time.LocalDateTime;

public record StatusUpdateResponse(
    String certificateId,
    String personId,
    String eventCode,
    String eventDisplayName,
    LocalDateTime eventTimestamp,
    int questionsSentTotal,
    int questionsReceivedTotal) {}
