package se.inera.intyg.intygmockservice.storelog.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LogTypeDTO {

  String logId;
  SystemDTO system;
  ActivityDTO activity;
  UserDTO user;
  List<ResourceDTO> resources;

  @Value
  @Builder
  public static class SystemDTO {

    String systemId;
    String systemName;
  }

  @Value
  @Builder
  public static class ActivityDTO {

    String activityType;
    String activityLevel;
    String activityArgs;
    LocalDateTime startDate;
    String purpose;
  }

  @Value
  @Builder
  public static class UserDTO {

    String userId;
    String assignment;
    CareProviderDTO careProvider;
    CareUnitDTO careUnit;
  }

  @Value
  @Builder
  public static class ResourceDTO {

    String resourceType;
    PatientDTO patient;
    CareProviderDTO careProvider;
    CareUnitDTO careUnit;
  }

  @Value
  @Builder
  public static class PatientDTO {

    String root;
    String extension;
  }

  @Value
  @Builder
  public static class CareProviderDTO {

    String careProviderId;
    String careProviderName;
  }

  @Value
  @Builder
  public static class CareUnitDTO {

    String careUnitId;
    String careUnitName;
  }
}
