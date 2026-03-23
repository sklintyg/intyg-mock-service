package se.inera.intyg.intygmockservice.application.navigation.message;

import java.time.LocalDateTime;

public record MessageResponse(
    String messageId,
    String certificateId,
    String personId,
    String recipient,
    String subject,
    String heading,
    String body,
    LocalDateTime sentTimestamp,
    String sentByStaffId,
    String sentByFullName) {}
