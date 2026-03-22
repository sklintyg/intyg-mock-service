package se.inera.intyg.intygmockservice.domain.navigation.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LogEntry {

  String logId;
  String systemId;
  String systemName;
  String activityType;
  String certificateId;
  String purpose;
  LocalDateTime activityStart;
  String userId;
  String userAssignment;
  String careUnitId;
  String careProviderName;
}
