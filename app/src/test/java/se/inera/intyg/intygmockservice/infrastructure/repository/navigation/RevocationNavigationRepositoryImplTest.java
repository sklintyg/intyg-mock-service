package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.infrastructure.repository.RevokeCertificateRepository;
import se.inera.intyg.intygmockservice.infrastructure.xml.JaxbXmlMarshaller;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.IntygId;

@ExtendWith(MockitoExtension.class)
class RevocationNavigationRepositoryImplTest {

  @Mock private RevokeCertificateRepository revokeCertificateRepository;
  @Mock private JaxbXmlMarshaller xmlMarshaller;

  @InjectMocks private RevocationNavigationRepositoryImpl repository;

  private static RevokeCertificateType revocation(
      final String certificateId, final String personId) {
    final var intygsId = new IntygId();
    intygsId.setExtension(certificateId);

    final var pid = new se.riv.clinicalprocess.healthcond.certificate.types.v3.PersonId();
    pid.setExtension(personId);

    final var revoke = new RevokeCertificateType();
    revoke.setIntygsId(intygsId);
    revoke.setPatientPersonId(pid);
    revoke.setSkickatTidpunkt(LocalDateTime.of(2024, 11, 9, 7, 40, 13));
    revoke.setMeddelande("Revoked reason");
    return revoke;
  }

  @Test
  void findByCertificateId_ShouldReturnRevocationWhenFound() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    final var revoke = revocation("cert-001", "191212121212");
    when(revokeCertificateRepository.findByCertificateId("cert-001"))
        .thenReturn(Optional.of(revoke));

    final var result = repository.findByCertificateId("cert-001");

    assertTrue(result.isPresent());
    assertEquals("cert-001", result.get().getCertificateId());
    assertEquals(PersonId.of("191212121212"), result.get().getPersonId());
    assertEquals("Revoked reason", result.get().getReason());
  }

  @Test
  void findByCertificateId_ShouldReturnEmptyWhenNotFound() {
    when(revokeCertificateRepository.findByCertificateId("unknown")).thenReturn(Optional.empty());

    assertTrue(repository.findByCertificateId("unknown").isEmpty());
  }

  @Test
  void findByPersonId_ShouldReturnMatchingRevocations() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    final var revoke = revocation("cert-001", "191212121212");
    when(revokeCertificateRepository.findByPersonId("191212121212")).thenReturn(List.of(revoke));

    final var result = repository.findByPersonId(PersonId.of("191212121212"));

    assertEquals(1, result.size());
    assertEquals("cert-001", result.get(0).getCertificateId());
    assertEquals(PersonId.of("191212121212"), result.get(0).getPersonId());
  }

  @Test
  void findByPersonId_ShouldReturnEmptyWhenNoMatches() {
    when(revokeCertificateRepository.findByPersonId("191212121212")).thenReturn(List.of());

    assertTrue(repository.findByPersonId(PersonId.of("191212121212")).isEmpty());
  }

  @Test
  void findByCertificateId_ShouldNormalizePersonId() {
    when(xmlMarshaller.marshal(any())).thenReturn("<xml/>");
    final var revoke = revocation("cert-001", "19121212-1212");
    when(revokeCertificateRepository.findByCertificateId("cert-001"))
        .thenReturn(Optional.of(revoke));

    final var result = repository.findByCertificateId("cert-001");

    assertTrue(result.isPresent());
    assertEquals(PersonId.of("19121212-1212"), result.get().getPersonId());
  }
}
