package se.inera.intyg.intygmockservice.storelog.converter;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO;
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO.CareUnitDTO;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;

@Component
public class StoreLogTypeConverter {

  /**
   * Converts a StoreLogType object to a list of LogTypeDTO objects.
   *
   * @param storeLogType the StoreLogType object to convert
   * @return a list of LogTypeDTO objects
   */
  public List<LogTypeDTO> convertToLogTypeDTO(StoreLogType storeLogType) {
    return storeLogType.getLog().stream()
        .map(
            log ->
                new LogTypeDTO(
                    log.getLogId(),
                    new LogTypeDTO.SystemDTO(
                        log.getSystem().getSystemId(), log.getSystem().getSystemName()),
                    new LogTypeDTO.ActivityDTO(
                        log.getActivity().getActivityType(),
                        log.getActivity().getActivityLevel(),
                        log.getActivity().getActivityArgs(),
                        log.getActivity().getStartDate(),
                        log.getActivity().getPurpose()),
                    new LogTypeDTO.UserDTO(
                        log.getUser().getUserId(),
                        log.getUser().getAssignment(),
                        new LogTypeDTO.CareProviderDTO(
                            log.getUser().getCareProvider().getCareProviderId(),
                            log.getUser().getCareProvider().getCareProviderName()),
                        new LogTypeDTO.CareUnitDTO(
                            log.getUser().getCareUnit().getCareUnitId(),
                            log.getUser().getCareUnit().getCareUnitName())),
                    log.getResources().getResource().stream()
                        .map(
                            resource ->
                                new LogTypeDTO.ResourceDTO(
                                    resource.getResourceType(),
                                    new LogTypeDTO.PatientDTO(
                                        resource.getPatient().getPatientId().getRoot(),
                                        resource.getPatient().getPatientId().getExtension()),
                                    new LogTypeDTO.CareProviderDTO(
                                        resource.getCareProvider().getCareProviderId(),
                                        resource.getCareProvider().getCareProviderName()),
                                    new CareUnitDTO(
                                        resource.getCareUnit().getCareUnitId(),
                                        resource.getCareUnit().getCareUnitName())))
                        .toList()))
        .collect(Collectors.toList());
  }
}
