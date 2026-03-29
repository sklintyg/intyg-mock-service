package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.infrastructure.repository.StoreLogTypeRepository;
import se.inera.intyg.intygmockservice.infrastructure.xml.JaxbXmlMarshaller;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;
import se.riv.informationsecurity.auditing.log.v2.ActivityType;
import se.riv.informationsecurity.auditing.log.v2.CareProviderType;
import se.riv.informationsecurity.auditing.log.v2.CareUnitType;
import se.riv.informationsecurity.auditing.log.v2.LogType;
import se.riv.informationsecurity.auditing.log.v2.SystemType;
import se.riv.informationsecurity.auditing.log.v2.UserType;

@ExtendWith(MockitoExtension.class)
class LogEntryNavigationRepositoryImplTest {

  @Mock private StoreLogTypeRepository storeLogTypeRepository;
  @Mock private JaxbXmlMarshaller xmlMarshaller;

  @InjectMocks private AuditLogEntryNavigationRepositoryImpl repository;

  private static StoreLogType soapLog(final LogType... logs) {
    final var storeLog = new StoreLogType();
    for (final var log : logs) {
      storeLog.getLog().add(log);
    }
    return storeLog;
  }

  private static LogType logType(final String certificateId) {
    final var system = new SystemType();
    system.setSystemId("WEBCERT");
    system.setSystemName("Webcert");

    final var activity = new ActivityType();
    activity.setActivityType("Läsa");
    activity.setActivityLevel(certificateId);
    activity.setPurpose("CARE_TREATMENT");
    activity.setStartDate(LocalDateTime.of(2024, 11, 9, 7, 40, 13));

    final var careProvider = new CareProviderType();
    careProvider.setCareProviderId("ALFA");
    careProvider.setCareProviderName("Alfa Regionen");

    final var careUnit = new CareUnitType();
    careUnit.setCareUnitId("ALMC");
    careUnit.setCareUnitName("Alfa Medicincentrum");

    final var user = new UserType();
    user.setUserId("it-user-001");
    user.setAssignment("Läkare");
    user.setCareProvider(careProvider);
    user.setCareUnit(careUnit);

    final var log = new LogType();
    log.setLogId("it-log-001");
    log.setSystem(system);
    log.setActivity(activity);
    log.setUser(user);
    return log;
  }

  @Test
  void findAll_ShouldReturnAllLogEntries() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    when(storeLogTypeRepository.findAll()).thenReturn(List.of(soapLog(logType("cert-001"))));

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
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    when(storeLogTypeRepository.findAll())
        .thenReturn(List.of(soapLog(logType("cert-001"), logType("cert-002"))));

    assertEquals(2, repository.findAll().size());
  }

  @Test
  void findAll_ShouldReturnEmptyWhenNoData() {
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  void findByCertificateId_ShouldReturnMatchingLogEntries() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    when(storeLogTypeRepository.findAll()).thenReturn(List.of(soapLog(logType("cert-001"))));

    final var result = repository.findByCertificateId("cert-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findByCertificateId_ShouldFilterOutNonMatchingEntries() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    when(storeLogTypeRepository.findAll())
        .thenReturn(List.of(soapLog(logType("cert-001"), logType("cert-002"))));

    final var result = repository.findByCertificateId("cert-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findByCertificateId_ShouldReturnEmptyWhenNoMatches() {
    when(storeLogTypeRepository.findAll()).thenReturn(List.of(soapLog(logType("cert-001"))));

    assertTrue(repository.findByCertificateId("unknown").isEmpty());
  }

  @Test
  void findById_ShouldReturnMatchingLogEntry() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    when(storeLogTypeRepository.findAll()).thenReturn(List.of(soapLog(logType("cert-001"))));

    final var result = repository.findById("it-log-001");

    assertTrue(result.isPresent());
    assertEquals("it-log-001", result.get().getLogId());
  }

  @Test
  void findById_ShouldReturnEmptyWhenNotFound() {
    when(storeLogTypeRepository.findAll()).thenReturn(List.of(soapLog(logType("cert-001"))));

    assertTrue(repository.findById("unknown").isEmpty());
  }
}
