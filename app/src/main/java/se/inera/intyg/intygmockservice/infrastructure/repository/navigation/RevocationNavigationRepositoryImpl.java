package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.model.Revocation;
import se.inera.intyg.intygmockservice.domain.navigation.repository.RevocationNavigationRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.RevokeCertificateRepository;
import se.inera.intyg.intygmockservice.infrastructure.xml.JaxbXmlMarshaller;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;

@Repository
@RequiredArgsConstructor
public class RevocationNavigationRepositoryImpl implements RevocationNavigationRepository {

  private final RevokeCertificateRepository revokeCertificateRepository;
  private final JaxbXmlMarshaller xmlMarshaller;

  @Override
  public Optional<Revocation> findByCertificateId(final String certificateId) {
    return revokeCertificateRepository.findByCertificateId(certificateId).map(this::toRevocation);
  }

  @Override
  public List<Revocation> findByPersonId(final PersonId personId) {
    return revokeCertificateRepository.findByPersonId(personId.normalized()).stream()
        .map(this::toRevocation)
        .toList();
  }

  private Revocation toRevocation(final RevokeCertificateType source) {
    final var skickatAv = source.getSkickatAv();
    final var staffId =
        skickatAv != null && skickatAv.getPersonalId() != null
            ? skickatAv.getPersonalId().getExtension()
            : null;
    final var staffName = skickatAv != null ? skickatAv.getFullstandigtNamn() : null;

    return Revocation.builder()
        .certificateId(source.getIntygsId() != null ? source.getIntygsId().getExtension() : null)
        .personId(
            source.getPatientPersonId() != null
                ? PersonId.of(source.getPatientPersonId().getExtension())
                : null)
        .revokedAt(source.getSkickatTidpunkt())
        .reason(source.getMeddelande())
        .revokedByStaffId(staffId)
        .revokedByFullName(staffName)
        .sourceXml(xmlMarshaller.marshal(source))
        .build();
  }
}
