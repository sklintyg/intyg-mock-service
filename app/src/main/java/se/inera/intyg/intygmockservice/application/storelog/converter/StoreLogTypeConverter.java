package se.inera.intyg.intygmockservice.storelog.converter;

import java.util.List;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO;
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO.ActivityDTO;
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO.CareProviderDTO;
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO.CareUnitDTO;
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO.PatientDTO;
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO.ResourceDTO;
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO.SystemDTO;
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO.UserDTO;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;

@Component
public class StoreLogTypeConverter {

  public List<LogTypeDTO> convertToLogTypeDTO(StoreLogType storeLogType) {
    return storeLogType.getLog().stream()
        .map(
            log ->
                LogTypeDTO.builder()
                    .logId(log.getLogId())
                    .system(
                        SystemDTO.builder()
                            .systemId(log.getSystem().getSystemId())
                            .systemName(log.getSystem().getSystemName())
                            .build())
                    .activity(
                        ActivityDTO.builder()
                            .activityType(log.getActivity().getActivityType())
                            .activityLevel(log.getActivity().getActivityLevel())
                            .activityArgs(log.getActivity().getActivityArgs())
                            .startDate(log.getActivity().getStartDate())
                            .purpose(log.getActivity().getPurpose())
                            .build())
                    .user(
                        UserDTO.builder()
                            .userId(log.getUser().getUserId())
                            .assignment(log.getUser().getAssignment())
                            .careProvider(
                                CareProviderDTO.builder()
                                    .careProviderId(
                                        log.getUser().getCareProvider().getCareProviderId())
                                    .careProviderName(
                                        log.getUser().getCareProvider().getCareProviderName())
                                    .build())
                            .careUnit(
                                CareUnitDTO.builder()
                                    .careUnitId(log.getUser().getCareUnit().getCareUnitId())
                                    .careUnitName(log.getUser().getCareUnit().getCareUnitName())
                                    .build())
                            .build())
                    .resources(
                        log.getResources().getResource().stream()
                            .map(
                                resource ->
                                    ResourceDTO.builder()
                                        .resourceType(resource.getResourceType())
                                        .patient(
                                            PatientDTO.builder()
                                                .root(
                                                    resource.getPatient().getPatientId().getRoot())
                                                .extension(
                                                    resource
                                                        .getPatient()
                                                        .getPatientId()
                                                        .getExtension())
                                                .build())
                                        .careProvider(
                                            CareProviderDTO.builder()
                                                .careProviderId(
                                                    resource.getCareProvider().getCareProviderId())
                                                .careProviderName(
                                                    resource
                                                        .getCareProvider()
                                                        .getCareProviderName())
                                                .build())
                                        .careUnit(
                                            CareUnitDTO.builder()
                                                .careUnitId(resource.getCareUnit().getCareUnitId())
                                                .careUnitName(
                                                    resource.getCareUnit().getCareUnitName())
                                                .build())
                                        .build())
                            .toList())
                    .build())
        .toList();
  }
}
