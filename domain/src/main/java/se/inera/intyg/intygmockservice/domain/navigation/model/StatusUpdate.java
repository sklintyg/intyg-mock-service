package se.inera.intyg.intygmockservice.domain.navigation.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StatusUpdate {

  String certificateId;
  PersonId personId;
  String eventCode;
  String eventDisplayName;
  LocalDateTime eventTimestamp;
  int questionsSentTotal;
  int questionsReceivedTotal;
  String sourceXml;
}
