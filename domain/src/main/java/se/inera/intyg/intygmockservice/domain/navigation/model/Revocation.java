package se.inera.intyg.intygmockservice.domain.navigation.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Revocation {

  String certificateId;
  PersonId personId;
  LocalDateTime revokedAt;
  String reason;
  String revokedByStaffId;
  String revokedByFullName;
  String sourceXml;
}
