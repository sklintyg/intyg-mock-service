package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.application.common.dto.PatientDTO.PersonId;
import se.inera.intyg.intygmockservice.application.revokecertificate.converter.RevokeCertificateConverter;
import se.inera.intyg.intygmockservice.application.revokecertificate.dto.RevokeCertificateDTO;
import se.inera.intyg.intygmockservice.infrastructure.repository.RevokeCertificateRepository;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;

@ExtendWith(MockitoExtension.class)
class RevocationNavigationRepositoryImplTest {

  @Mock private RevokeCertificateRepository revokeCertificateRepository;
  @Mock private RevokeCertificateConverter revokeCertificateConverter;

  @InjectMocks private RevocationNavigationRepositoryImpl repository;

  private static RevokeCertificateType soapRevocation() {
    return new RevokeCertificateType();
  }

  private static RevokeCertificateDTO dto(final String certificateId, final String personId) {
    return RevokeCertificateDTO.builder()
        .intygsId(IntygsId.builder().extension(certificateId).build())
        .patientPersonId(PersonId.builder().extension(personId).build())
        .skickatTidpunkt(LocalDateTime.of(2024, 11, 9, 7, 40, 13))
        .meddelande("Revoked reason")
        .build();
  }

  @Test
  void findByCertificateId_ShouldReturnRevocationWhenFound() {
    final var soap = soapRevocation();
    final var dto = dto("cert-001", "191212121212");

    when(revokeCertificateRepository.findByCertificateId("cert-001")).thenReturn(Optional.of(soap));
    when(revokeCertificateConverter.convert(soap)).thenReturn(dto);

    final var result = repository.findByCertificateId("cert-001");

    assertTrue(result.isPresent());
    assertEquals("cert-001", result.get().getCertificateId());
    assertEquals("191212121212", result.get().getPersonId());
    assertEquals("Revoked reason", result.get().getReason());
  }

  @Test
  void findByCertificateId_ShouldReturnEmptyWhenNotFound() {
    when(revokeCertificateRepository.findByCertificateId("unknown")).thenReturn(Optional.empty());

    final var result = repository.findByCertificateId("unknown");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByPersonId_ShouldReturnMatchingRevocations() {
    final var soap = soapRevocation();
    final var dto = dto("cert-001", "191212121212");

    when(revokeCertificateRepository.findByPersonId("191212121212")).thenReturn(List.of(soap));
    when(revokeCertificateConverter.convert(soap)).thenReturn(dto);

    final var result = repository.findByPersonId("191212121212");

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
    assertEquals("191212121212", result.get(0).getPersonId());
  }

  @Test
  void findByPersonId_ShouldReturnEmptyWhenNoMatches() {
    when(revokeCertificateRepository.findByPersonId("191212121212")).thenReturn(List.of());

    final var result = repository.findByPersonId("191212121212");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByCertificateId_ShouldNormalizePersonId() {
    final var soap = soapRevocation();
    final var dto = dto("cert-001", "19121212-1212");

    when(revokeCertificateRepository.findByCertificateId("cert-001")).thenReturn(Optional.of(soap));
    when(revokeCertificateConverter.convert(soap)).thenReturn(dto);

    final var result = repository.findByCertificateId("cert-001");

    assertTrue(result.isPresent());
    assertEquals("191212121212", result.get().getPersonId());
  }
}
