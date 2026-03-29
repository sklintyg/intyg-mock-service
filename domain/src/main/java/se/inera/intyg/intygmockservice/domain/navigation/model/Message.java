package se.inera.intyg.intygmockservice.domain.navigation.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Message {

  String messageId;
  String certificateId;
  PersonId personId;
  String recipient;
  String subject;
  String heading;
  String body;
  LocalDateTime sentTimestamp;
  String sentByStaffId;
  String sentByFullName;
  String sourceXml;
}
