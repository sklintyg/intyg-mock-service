package se.inera.intyg.intygmockservice.application.navigation.revocation;

import java.time.LocalDateTime;

public record RevocationResponse(
    String certificateId,
    String personId,
    LocalDateTime revokedAt,
    String reason,
    String revokedByStaffId,
    String revokedByFullName) {}
