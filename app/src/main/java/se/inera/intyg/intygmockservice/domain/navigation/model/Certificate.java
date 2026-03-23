package se.inera.intyg.intygmockservice.domain.navigation.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Certificate {

  String certificateId;
  String certificateType;
  String certificateTypeDisplayName;
  LocalDateTime signingTimestamp;
  LocalDateTime sentTimestamp;
  String version;
  String logicalAddress;
  Patient patient;
  Staff issuedBy;
}
