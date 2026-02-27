package se.inera.intyg.intygmockservice.storelog.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogTypeDTO {

  private String logId;
  private SystemDTO system;
  private ActivityDTO activity;
  private UserDTO user;
  private List<ResourceDTO> resources;

  @Data
  @AllArgsConstructor
  public static class SystemDTO {

    private String systemId;
    private String systemName;
  }

  @Data
  @AllArgsConstructor
  public static class ActivityDTO {

    private String activityType;
    private String activityLevel;
    private String activityArgs;
    private LocalDateTime startDate;
    private String purpose;
  }

  @Data
  @AllArgsConstructor
  public static class UserDTO {

    private String userId;
    private String assignment;
    private CareProviderDTO careProvider;
    private CareUnitDTO careUnit;
  }

  @Data
  @AllArgsConstructor
  public static class ResourceDTO {

    private String resourceType;
    private PatientDTO patient;
    private CareProviderDTO careProvider;
    private CareUnitDTO careUnit;
  }

  @Data
  @AllArgsConstructor
  public static class PatientDTO {

    private String root;
    private String extension;
  }

  @Data
  @AllArgsConstructor
  public static class CareProviderDTO {

    private String careProviderId;
    private String careProviderName;
  }

  @Data
  @AllArgsConstructor
  public static class CareUnitDTO {

    private String careUnitId;
    private String careUnitName;
  }
}
