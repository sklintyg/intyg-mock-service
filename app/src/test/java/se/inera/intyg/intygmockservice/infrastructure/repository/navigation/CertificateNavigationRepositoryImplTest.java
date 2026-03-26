package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import se.inera.intyg.intygmockservice.application.common.dto.HoSPersonalDTO;
import se.inera.intyg.intygmockservice.application.common.dto.HoSPersonalDTO.EnhetDTO;
import se.inera.intyg.intygmockservice.application.common.dto.HoSPersonalDTO.EnhetDTO.HsaIdDTO;
import se.inera.intyg.intygmockservice.application.common.dto.HoSPersonalDTO.EnhetDTO.VardgivareDTO;
import se.inera.intyg.intygmockservice.application.common.dto.HoSPersonalDTO.PersonalIdDTO;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.application.common.dto.PatientDTO;
import se.inera.intyg.intygmockservice.application.registercertificate.converter.RegisterCertificateConverter;
import se.inera.intyg.intygmockservice.application.registercertificate.dto.RegisterCertificateDTO;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.infrastructure.repository.CertificateStatusUpdateForCareRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.RegisterCertificateRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.RevokeCertificateRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.SendMessageToRecipientRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.StoreLogTypeRepository;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.IntygId;

@ExtendWith(MockitoExtension.class)
class CertificateNavigationRepositoryImplTest {

  @Mock private RegisterCertificateRepository registerCertificateRepository;
  @Mock private RevokeCertificateRepository revokeCertificateRepository;
  @Mock private SendMessageToRecipientRepository sendMessageToRecipientRepository;
  @Mock private CertificateStatusUpdateForCareRepository statusUpdateRepository;
  @Mock private StoreLogTypeRepository storeLogTypeRepository;
  @Mock private RegisterCertificateConverter registerCertificateConverter;

  @InjectMocks private CertificateNavigationRepositoryImpl repository;

  @Test
  void findAll_ShouldReturnCertificateFromRegistration() {
    final var rawType = new RegisterCertificateType();
    final var dto = buildRegisterCertificateDTO("cert-001", "191212121212");

    when(registerCertificateRepository.findAll()).thenReturn(List.of(rawType));
    when(registerCertificateConverter.convert(any(RegisterCertificateType.class))).thenReturn(dto);
    when(revokeCertificateRepository.findAll()).thenReturn(List.of());
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    final var result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
    assertNotNull(result.get(0).getPatient());
    assertEquals(PersonId.of("191212121212"), result.get(0).getPatient().getPersonId());
  }

  @Test
  void findAll_ShouldDeduplicateWhenCertificateIsInBothRegistrationAndRevocation() {
    final var rawType = new RegisterCertificateType();
    final var dto = buildRegisterCertificateDTO("cert-001", "191212121212");

    when(registerCertificateRepository.findAll()).thenReturn(List.of(rawType));
    when(registerCertificateConverter.convert(any(RegisterCertificateType.class))).thenReturn(dto);
    when(revokeCertificateRepository.findAll())
        .thenReturn(List.of(buildRevokeType("cert-001", "191212121212")));
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    final var result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findAll_ShouldIncludeRevocationOnlyCertificateWithPersonId() {
    when(registerCertificateRepository.findAll()).thenReturn(List.of());
    when(revokeCertificateRepository.findAll())
        .thenReturn(List.of(buildRevokeType("cert-revoke-only", "191212121212")));
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    final var result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals("cert-revoke-only", result.get(0).getCertificateId());
    assertNotNull(result.get(0).getPatient());
    assertEquals(PersonId.of("191212121212"), result.get(0).getPatient().getPersonId());
    assertNull(result.get(0).getIssuedBy());
  }

  @Test
  void findAll_ShouldNormalizePersonIdHyphensFromRevocation() {
    when(registerCertificateRepository.findAll()).thenReturn(List.of());
    when(revokeCertificateRepository.findAll())
        .thenReturn(List.of(buildRevokeType("cert-123", "19121212-1212")));
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    final var result = repository.findAll();

    assertEquals(PersonId.of("19121212-1212"), result.get(0).getPatient().getPersonId());
  }

  @Test
  void findAll_ShouldReturnEmptyWhenNoData() {
    when(registerCertificateRepository.findAll()).thenReturn(List.of());
    when(revokeCertificateRepository.findAll()).thenReturn(List.of());
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  void findById_ShouldReturnCertificateWhenPresentInRevocation() {
    when(registerCertificateRepository.findAll()).thenReturn(List.of());
    when(revokeCertificateRepository.findAll())
        .thenReturn(List.of(buildRevokeType("cert-001", "191212121212")));
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    final var result = repository.findById("cert-001");

    assertTrue(result.isPresent());
    assertEquals("cert-001", result.get().getCertificateId());
  }

  @Test
  void findById_ShouldReturnEmptyWhenNotFound() {
    when(registerCertificateRepository.findAll()).thenReturn(List.of());
    when(revokeCertificateRepository.findAll()).thenReturn(List.of());
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    assertTrue(repository.findById("nonexistent").isEmpty());
  }

  @Test
  void findByPersonId_ShouldReturnOnlyCertificatesMatchingPersonId() {
    when(registerCertificateRepository.findAll()).thenReturn(List.of());
    when(revokeCertificateRepository.findAll())
        .thenReturn(
            List.of(
                buildRevokeType("cert-001", "191212121212"),
                buildRevokeType("cert-002", "999999999999")));
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    final var result = repository.findByPersonId("191212121212");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findByUnitId_ShouldReturnCertificatesForMatchingUnit() {
    final var rawType = new RegisterCertificateType();
    final var dto = buildRegisterCertificateDTO("cert-001", "191212121212");

    when(registerCertificateRepository.findAll()).thenReturn(List.of(rawType));
    when(registerCertificateConverter.convert(any(RegisterCertificateType.class))).thenReturn(dto);
    when(revokeCertificateRepository.findAll()).thenReturn(List.of());
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    final var result = repository.findByUnitId("unit-hsa-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findByUnitId_ShouldReturnEmptyWhenNoMatchingUnit() {
    final var rawType = new RegisterCertificateType();
    final var dto = buildRegisterCertificateDTO("cert-001", "191212121212");

    when(registerCertificateRepository.findAll()).thenReturn(List.of(rawType));
    when(registerCertificateConverter.convert(any(RegisterCertificateType.class))).thenReturn(dto);
    when(revokeCertificateRepository.findAll()).thenReturn(List.of());
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    assertTrue(repository.findByUnitId("unknown-unit").isEmpty());
  }

  @Test
  void findByStaffId_ShouldReturnCertificatesForMatchingStaff() {
    final var rawType = new RegisterCertificateType();
    final var dto = buildRegisterCertificateDTO("cert-001", "191212121212");

    when(registerCertificateRepository.findAll()).thenReturn(List.of(rawType));
    when(registerCertificateConverter.convert(any(RegisterCertificateType.class))).thenReturn(dto);
    when(revokeCertificateRepository.findAll()).thenReturn(List.of());
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll()).thenReturn(List.of());

    final var result = repository.findByStaffId("staff-hsa-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findAll_ShouldExcludeStoreLogEntryWhenActivityLevelIsSingleCharLevelCode() {
    when(registerCertificateRepository.findAll()).thenReturn(List.of());
    when(revokeCertificateRepository.findAll()).thenReturn(List.of());
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll())
        .thenReturn(List.of(buildStoreLogType("2", "191212121212")));

    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  void findAll_ShouldIncludeStoreLogCertificateWithPersonIdFromResource() {
    when(registerCertificateRepository.findAll()).thenReturn(List.of());
    when(revokeCertificateRepository.findAll()).thenReturn(List.of());
    when(sendMessageToRecipientRepository.findAll()).thenReturn(List.of());
    when(statusUpdateRepository.findAll()).thenReturn(List.of());
    when(storeLogTypeRepository.findAll())
        .thenReturn(List.of(buildStoreLogType("cert-from-log-001", "191212121212")));

    final var result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals("cert-from-log-001", result.get(0).getCertificateId());
    assertNotNull(result.get(0).getPatient());
    assertEquals(PersonId.of("191212121212"), result.get(0).getPatient().getPersonId());
  }

  // --- helpers ---

  private static RegisterCertificateDTO buildRegisterCertificateDTO(
      final String certId, final String personId) {
    return RegisterCertificateDTO.builder()
        .intyg(
            IntygDTO.builder()
                .intygsId(IntygsId.builder().extension(certId).build())
                .signeringstidpunkt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .patient(
                    PatientDTO.builder()
                        .personId(PatientDTO.PersonId.builder().extension(personId).build())
                        .fornamn("Test")
                        .efternamn("Testsson")
                        .build())
                .skapadAv(
                    HoSPersonalDTO.builder()
                        .personalId(PersonalIdDTO.builder().extension("staff-hsa-001").build())
                        .fullstandigtNamn("Dr Test")
                        .enhet(
                            EnhetDTO.builder()
                                .enhetsId(HsaIdDTO.builder().extension("unit-hsa-001").build())
                                .enhetsnamn("Test Unit")
                                .vardgivare(
                                    VardgivareDTO.builder()
                                        .vardgivareId(
                                            HsaIdDTO.builder().extension("vg-001").build())
                                        .vardgivarnamn("Test Vardgivare")
                                        .build())
                                .build())
                        .build())
                .build())
        .build();
  }

  private static se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType
      buildStoreLogType(final String activityLevel, final String personId) {
    final var patientId = new se.riv.informationsecurity.auditing.log.v2.IIType();
    patientId.setExtension(personId);
    final var patient = new se.riv.informationsecurity.auditing.log.v2.PatientType();
    patient.setPatientId(patientId);
    final var resource = new se.riv.informationsecurity.auditing.log.v2.ResourceType();
    resource.setPatient(patient);
    final var resources = new se.riv.informationsecurity.auditing.log.v2.ResourcesType();
    resources.getResource().add(resource);
    final var activity = new se.riv.informationsecurity.auditing.log.v2.ActivityType();
    activity.setActivityLevel(activityLevel);
    final var log = new se.riv.informationsecurity.auditing.log.v2.LogType();
    log.setResources(resources);
    log.setActivity(activity);
    final var storeLog =
        new se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType();
    storeLog.getLog().add(log);
    return storeLog;
  }

  private static RevokeCertificateType buildRevokeType(final String certId, final String personId) {
    final var revoke = new RevokeCertificateType();
    final var intygsId = new IntygId();
    intygsId.setExtension(certId);
    revoke.setIntygsId(intygsId);
    final var pid = new se.riv.clinicalprocess.healthcond.certificate.types.v3.PersonId();
    pid.setExtension(personId);
    revoke.setPatientPersonId(pid);
    return revoke;
  }
}
