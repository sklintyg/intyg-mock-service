package se.inera.intyg.intygmockservice.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.infrastructure.config.properties.AppProperties;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.Handelsekod;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.IntygId;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.PersonId;
import se.riv.clinicalprocess.healthcond.certificate.v3.Handelse;
import se.riv.clinicalprocess.healthcond.certificate.v3.Intyg;
import se.riv.clinicalprocess.healthcond.certificate.v3.Patient;

class CertificateStatusUpdateForCareRepositoryTest {

  private static final AppProperties APP_PROPERTIES =
      new AppProperties(
          new AppProperties.Repository(
              new AppProperties.RepositoryConfig(100),
              new AppProperties.RepositoryConfig(100),
              new AppProperties.RepositoryConfig(100),
              new AppProperties.RepositoryConfig(100),
              new AppProperties.RepositoryConfig(100)));

  private static final String LOGICAL_ADDRESS = "TSTNMT2321000156-ALMC";

  private CertificateStatusUpdateForCareRepository repository;

  @BeforeEach
  void setUp() {
    repository = new CertificateStatusUpdateForCareRepository(APP_PROPERTIES);
  }

  @Test
  void findByCertificateId_ShouldReturnMatchingEntry() {
    final var type = buildType("cert-001", "191212121212", "SKAPAT");
    repository.add(LOGICAL_ADDRESS, type);

    assertThat(repository.findByCertificateId("cert-001")).containsExactly(type);
  }

  @Test
  void findByCertificateId_ShouldReturnEmptyWhenNoMatch() {
    repository.add(LOGICAL_ADDRESS, buildType("cert-001", "191212121212", "SKAPAT"));

    assertThat(repository.findByCertificateId("cert-999")).isEmpty();
  }

  @Test
  void findByCertificateId_ShouldReturnAllMatchingEntries() {
    final var type1 = buildType("cert-001", "191212121212", "SKAPAT");
    final var type2 = buildType("cert-001", "191212121212", "SKICKA");
    repository.add(LOGICAL_ADDRESS, type1);
    repository.add(LOGICAL_ADDRESS, type2);

    assertThat(repository.findByCertificateId("cert-001")).hasSize(2);
  }

  @Test
  void findByLogicalAddress_ShouldReturnMatchingEntries() {
    final var type = buildType("cert-001", "191212121212", "SKAPAT");
    repository.add(LOGICAL_ADDRESS, type);

    assertThat(repository.findByLogicalAddress(LOGICAL_ADDRESS)).containsExactly(type);
  }

  @Test
  void findByLogicalAddress_ShouldReturnEmptyWhenNoMatch() {
    repository.add(LOGICAL_ADDRESS, buildType("cert-001", "191212121212", "SKAPAT"));

    assertThat(repository.findByLogicalAddress("UNKNOWN-ADDRESS")).isEmpty();
  }

  @Test
  void findByPersonId_ShouldReturnMatchingEntry() {
    final var type = buildType("cert-001", "191212121212", "SKAPAT");
    repository.add(LOGICAL_ADDRESS, type);

    assertThat(repository.findByPersonId("191212121212")).containsExactly(type);
  }

  @Test
  void findByPersonId_ShouldReturnEmptyWhenNoMatch() {
    repository.add(LOGICAL_ADDRESS, buildType("cert-001", "191212121212", "SKAPAT"));

    assertThat(repository.findByPersonId("000000000000")).isEmpty();
  }

  @Test
  void findByEventCode_ShouldReturnMatchingEntry() {
    final var type = buildType("cert-001", "191212121212", "SKAPAT");
    repository.add(LOGICAL_ADDRESS, type);

    assertThat(repository.findByEventCode("SKAPAT")).containsExactly(type);
  }

  @Test
  void findByEventCode_ShouldReturnEmptyWhenNoMatch() {
    repository.add(LOGICAL_ADDRESS, buildType("cert-001", "191212121212", "SKAPAT"));

    assertThat(repository.findByEventCode("UNKNOWN")).isEmpty();
  }

  @Test
  void deleteByCertificateId_ShouldRemoveMatchingAndLeaveRest() {
    final var type1 = buildType("cert-001", "191212121212", "SKAPAT");
    final var type2 = buildType("cert-002", "191212121212", "SKICKA");
    repository.add(LOGICAL_ADDRESS, type1);
    repository.add(LOGICAL_ADDRESS, type2);

    repository.deleteByCertificateId("cert-001");

    assertThat(repository.findAll()).containsExactly(type2);
  }

  private static CertificateStatusUpdateForCareType buildType(
      String certId, String personId, String eventCode) {
    final var intygsId = new IntygId();
    intygsId.setExtension(certId);

    final var pid = new PersonId();
    pid.setExtension(personId);

    final var patient = new Patient();
    patient.setPersonId(pid);

    final var intyg = new Intyg();
    intyg.setIntygsId(intygsId);
    intyg.setPatient(patient);

    final var handelsekod = new Handelsekod();
    handelsekod.setCode(eventCode);

    final var handelse = new Handelse();
    handelse.setHandelsekod(handelsekod);

    final var type = new CertificateStatusUpdateForCareType();
    type.setIntyg(intyg);
    type.setHandelse(handelse);

    return type;
  }
}
