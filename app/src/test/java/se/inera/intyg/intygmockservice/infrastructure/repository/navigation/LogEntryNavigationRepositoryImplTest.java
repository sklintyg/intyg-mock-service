package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.application.storelog.converter.StoreLogTypeConverter;
import se.inera.intyg.intygmockservice.application.storelog.dto.LogTypeDTO;
import se.inera.intyg.intygmockservice.infrastructure.repository.StoreLogTypeRepository;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;

@ExtendWith(MockitoExtension.class)
class LogEntryNavigationRepositoryImplTest {

  @Mock private StoreLogTypeRepository storeLogTypeRepository;
  @Mock private StoreLogTypeConverter converter;

  @InjectMocks private LogEntryNavigationRepositoryImpl repository;

  private static StoreLogType soapLog() {
    return new StoreLogType();
  }

  private static LogTypeDTO dto(final String certificateId) {
    return LogTypeDTO.builder()
        .logId("it-log-001")
        .system(LogTypeDTO.SystemDTO.builder().systemId("WEBCERT").systemName("Webcert").build())
        .activity(
            LogTypeDTO.ActivityDTO.builder()
                .activityType("Läsa")
                .activityLevel(certificateId)
                .purpose("CARE_TREATMENT")
                .startDate(LocalDateTime.of(2024, 11, 9, 7, 40, 13))
                .build())
        .user(
            LogTypeDTO.UserDTO.builder()
                .userId("it-user-001")
                .assignment("Läkare")
                .careProvider(
                    LogTypeDTO.CareProviderDTO.builder()
                        .careProviderId("ALFA")
                        .careProviderName("Alfa Regionen")
                        .build())
                .careUnit(
                    LogTypeDTO.CareUnitDTO.builder()
                        .careUnitId("ALMC")
                        .careUnitName("Alfa Medicincentrum")
                        .build())
                .build())
        .resources(List.of())
        .build();
  }

  @Test
  void findAll_ShouldReturnAllLogEntries() {
    final var soap = soapLog();
    final var dto = dto("cert-001");

    when(storeLogTypeRepository.findAll()).thenReturn(List.of(soap));
    when(converter.convertToLogTypeDTO(soap)).thenReturn(List.of(dto));

    final var result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals("it-log-001", result.get(0).getLogId());
    assertEquals("WEBCERT", result.get(0).getSystemId());
    assertEquals("Webcert", result.get(0).getSystemName());
    assertEquals("Läsa", result.get(0).getActivityType());
    assertEquals("cert-001", result.get(0).getCertificateId());
    assertEquals("CARE_TREATMENT", result.get(0).getPurpose());
    assertEquals("it-user-001", result.get(0).getUserId());
    assertEquals("Läkare", result.get(0).getUserAssignment());
    assertEquals("ALMC", result.get(0).getCareUnitId());
    assertEquals("Alfa Regionen", result.get(0).getCareProviderName());
  }

  @Test
  void findAll_ShouldFlatMapMultipleLogsPerStoreLogType() {
    final var soap = soapLog();

    when(storeLogTypeRepository.findAll()).thenReturn(List.of(soap));
    when(converter.convertToLogTypeDTO(soap)).thenReturn(List.of(dto("cert-001"), dto("cert-002")));

    assertEquals(2, repository.findAll().size());
  }

  @Test
  void findAll_ShouldReturnEmptyWhenNoData() {
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  void findByCertificateId_ShouldReturnMatchingLogEntries() {
    final var soap = soapLog();
    final var dto = dto("cert-001");

    when(storeLogTypeRepository.findAll()).thenReturn(List.of(soap));
    when(converter.convertToLogTypeDTO(soap)).thenReturn(List.of(dto));

    final var result = repository.findByCertificateId("cert-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findByCertificateId_ShouldFilterOutNonMatchingEntries() {
    final var soap = soapLog();

    when(storeLogTypeRepository.findAll()).thenReturn(List.of(soap));
    when(converter.convertToLogTypeDTO(soap)).thenReturn(List.of(dto("cert-001"), dto("cert-002")));

    final var result = repository.findByCertificateId("cert-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findByCertificateId_ShouldReturnEmptyWhenNoMatches() {
    final var soap = soapLog();

    when(storeLogTypeRepository.findAll()).thenReturn(List.of(soap));
    when(converter.convertToLogTypeDTO(soap)).thenReturn(List.of(dto("cert-001")));

    assertTrue(repository.findByCertificateId("unknown").isEmpty());
  }

  @Test
  void findById_ShouldReturnMatchingLogEntry() {
    final var soap = soapLog();
    final var dto = dto("cert-001");

    when(storeLogTypeRepository.findAll()).thenReturn(List.of(soap));
    when(converter.convertToLogTypeDTO(soap)).thenReturn(List.of(dto));

    final var result = repository.findById("it-log-001");

    assertTrue(result.isPresent());
    assertEquals("it-log-001", result.get().getLogId());
  }

  @Test
  void findById_ShouldReturnEmptyWhenNotFound() {
    final var soap = soapLog();

    when(storeLogTypeRepository.findAll()).thenReturn(List.of(soap));
    when(converter.convertToLogTypeDTO(soap)).thenReturn(List.of(dto("cert-001")));

    assertTrue(repository.findById("unknown").isEmpty());
  }
}
