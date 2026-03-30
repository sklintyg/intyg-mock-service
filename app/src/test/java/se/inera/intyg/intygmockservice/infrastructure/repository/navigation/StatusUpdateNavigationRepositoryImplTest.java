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
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.infrastructure.repository.CertificateStatusUpdateForCareRepository;
import se.inera.intyg.intygmockservice.infrastructure.xml.JaxbXmlMarshaller;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.Handelsekod;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.IntygId;
import se.riv.clinicalprocess.healthcond.certificate.v3.Arenden;
import se.riv.clinicalprocess.healthcond.certificate.v3.Handelse;
import se.riv.clinicalprocess.healthcond.certificate.v3.Intyg;
import se.riv.clinicalprocess.healthcond.certificate.v3.Patient;

@ExtendWith(MockitoExtension.class)
class StatusUpdateNavigationRepositoryImplTest {

  @Mock private CertificateStatusUpdateForCareRepository statusUpdateRepository;
  @Mock private JaxbXmlMarshaller xmlMarshaller;

  @InjectMocks private StatusUpdateNavigationRepositoryImpl repository;

  private static CertificateStatusUpdateForCareType statusUpdate(
      final String certificateId, final String personId) {
    final var intygsId = new IntygId();
    intygsId.setExtension(certificateId);

    final var pid = new se.riv.clinicalprocess.healthcond.certificate.types.v3.PersonId();
    pid.setExtension(personId);

    final var patient = new Patient();
    patient.setPersonId(pid);

    final var intyg = new Intyg();
    intyg.setIntygsId(intygsId);
    intyg.setPatient(patient);

    final var handelsekod = new Handelsekod();
    handelsekod.setCode("SKAPAT");
    handelsekod.setDisplayName("Intyg skapat");

    final var handelse = new Handelse();
    handelse.setHandelsekod(handelsekod);
    handelse.setTidpunkt(LocalDateTime.of(2024, 11, 9, 7, 40, 13));

    final var skickadeFragor = new Arenden();
    skickadeFragor.setTotalt(2);

    final var mottagnaFragor = new Arenden();
    mottagnaFragor.setTotalt(1);

    final var update = new CertificateStatusUpdateForCareType();
    update.setIntyg(intyg);
    update.setHandelse(handelse);
    update.setSkickadeFragor(skickadeFragor);
    update.setMottagnaFragor(mottagnaFragor);
    return update;
  }

  @Test
  void findAll_ShouldReturnAllStatusUpdates() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    when(statusUpdateRepository.findAll())
        .thenReturn(List.of(statusUpdate("cert-001", "191212121212")));

    final var result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
    assertEquals(PersonId.of("191212121212"), result.get(0).getPersonId());
    assertEquals("SKAPAT", result.get(0).getEventCode());
    assertEquals("Intyg skapat", result.get(0).getEventDisplayName());
    assertEquals(2, result.get(0).getQuestionsSentTotal());
    assertEquals(1, result.get(0).getQuestionsReceivedTotal());
  }

  @Test
  void findAll_ShouldReturnEmptyWhenNoData() {
    when(statusUpdateRepository.findAll()).thenReturn(List.of());

    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  void findByCertificateId_ShouldReturnMatchingStatusUpdates() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    when(statusUpdateRepository.findByCertificateId("cert-001"))
        .thenReturn(List.of(statusUpdate("cert-001", "191212121212")));

    final var result = repository.findByCertificateId("cert-001");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
  }

  @Test
  void findByCertificateId_ShouldReturnEmptyWhenNoMatches() {
    when(statusUpdateRepository.findByCertificateId("unknown")).thenReturn(List.of());

    assertTrue(repository.findByCertificateId("unknown").isEmpty());
  }

  @Test
  void findByPersonId_ShouldReturnMatchingStatusUpdates() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    when(statusUpdateRepository.findByPersonId("191212121212"))
        .thenReturn(List.of(statusUpdate("cert-001", "191212121212")));

    final var result = repository.findByPersonId(PersonId.of("191212121212"));

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
    assertEquals(PersonId.of("191212121212"), result.get(0).getPersonId());
  }

  @Test
  void findByPersonId_ShouldReturnEmptyWhenNoMatches() {
    when(statusUpdateRepository.findByPersonId("191212121212")).thenReturn(List.of());

    assertTrue(repository.findByPersonId(PersonId.of("191212121212")).isEmpty());
  }

  @Test
  void findByCertificateId_ShouldNormalizePersonId() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    when(statusUpdateRepository.findByCertificateId("cert-001"))
        .thenReturn(List.of(statusUpdate("cert-001", "19121212-1212")));

    final var result = repository.findByCertificateId("cert-001");

    assertEquals(PersonId.of("19121212-1212"), result.get(0).getPersonId());
  }
}
