package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.application.common.dto.PatientDTO;
import se.inera.intyg.intygmockservice.application.statusupdates.converter.CertificateStatusUpdateForCareConverter;
import se.inera.intyg.intygmockservice.application.statusupdates.dto.CertificateStatusUpdateForCareDTO;
import se.inera.intyg.intygmockservice.application.statusupdates.dto.CertificateStatusUpdateForCareDTO.Fragor;
import se.inera.intyg.intygmockservice.application.statusupdates.dto.CertificateStatusUpdateForCareDTO.Handelse;
import se.inera.intyg.intygmockservice.application.statusupdates.dto.CertificateStatusUpdateForCareDTO.Handelse.Handelsekod;
import se.inera.intyg.intygmockservice.infrastructure.repository.CertificateStatusUpdateForCareRepository;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;

@ExtendWith(MockitoExtension.class)
class StatusUpdateNavigationRepositoryImplTest {

  @Mock private CertificateStatusUpdateForCareRepository statusUpdateRepository;
  @Mock private CertificateStatusUpdateForCareConverter converter;

  @InjectMocks private StatusUpdateNavigationRepositoryImpl repository;

  private static CertificateStatusUpdateForCareType soapUpdate() {
    return new CertificateStatusUpdateForCareType();
  }

  private static CertificateStatusUpdateForCareDTO dto(
      final String certificateId, final String personId) {
    return CertificateStatusUpdateForCareDTO.builder()
        .intyg(
            IntygDTO.builder()
                .intygsId(IntygDTO.IntygsId.builder().extension(certificateId).build())
                .patient(
                    PatientDTO.builder()
                        .personId(PatientDTO.PersonId.builder().extension(personId).build())
                        .build())
                .build())
        .handelse(
            Handelse.builder()
                .handelsekod(
                    Handelsekod.builder().code("SKAPAT").displayName("Intyg skapat").build())
                .tidpunkt("2024-11-09T07:40:13")
                .build())
        .skickadeFragor(Fragor.builder().totalt(2).ejBesvarade(0).besvarade(0).hanterade(0).build())
        .mottagnaFragor(Fragor.builder().totalt(1).ejBesvarade(0).besvarade(0).hanterade(0).build())
        .build();
  }

  @Test
  void findAll_ShouldReturnAllStatusUpdates() {
    final var soap = soapUpdate();
    final var dto = dto("cert-001", "191212121212");

    when(statusUpdateRepository.findAll()).thenReturn(List.of(soap));
    when(converter.convert(soap)).thenReturn(dto);

    final var result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
    assertEquals("191212121212", result.get(0).getPersonId());
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
    final var soap = soapUpdate();
    final var dto = dto("cert-001", "191212121212");

    when(statusUpdateRepository.findByCertificateId("cert-001")).thenReturn(List.of(soap));
    when(converter.convert(soap)).thenReturn(dto);

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
    final var soap = soapUpdate();
    final var dto = dto("cert-001", "191212121212");

    when(statusUpdateRepository.findByPersonId("191212121212")).thenReturn(List.of(soap));
    when(converter.convert(soap)).thenReturn(dto);

    final var result = repository.findByPersonId("191212121212");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
    assertEquals("191212121212", result.get(0).getPersonId());
  }

  @Test
  void findByPersonId_ShouldReturnEmptyWhenNoMatches() {
    when(statusUpdateRepository.findByPersonId("191212121212")).thenReturn(List.of());

    assertTrue(repository.findByPersonId("191212121212").isEmpty());
  }

  @Test
  void findByCertificateId_ShouldNormalizePersonId() {
    final var soap = soapUpdate();
    final var dto = dto("cert-001", "19121212-1212");

    when(statusUpdateRepository.findByCertificateId("cert-001")).thenReturn(List.of(soap));
    when(converter.convert(soap)).thenReturn(dto);

    final var result = repository.findByCertificateId("cert-001");

    assertEquals("191212121212", result.get(0).getPersonId());
  }
}
