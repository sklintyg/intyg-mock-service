package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.application.revokecertificate.converter.RevokeCertificateConverter;
import se.inera.intyg.intygmockservice.application.revokecertificate.dto.RevokeCertificateDTO;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.model.Revocation;
import se.inera.intyg.intygmockservice.domain.navigation.repository.RevocationNavigationRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.RevokeCertificateRepository;

@Repository
@RequiredArgsConstructor
public class RevocationNavigationRepositoryImpl implements RevocationNavigationRepository {

  private final RevokeCertificateRepository revokeCertificateRepository;
  private final RevokeCertificateConverter revokeCertificateConverter;

  @Override
  public Optional<Revocation> findByCertificateId(final String certificateId) {
    return revokeCertificateRepository
        .findByCertificateId(certificateId)
        .map(revokeCertificateConverter::convert)
        .map(this::toRevocation);
  }

  @Override
  public List<Revocation> findByPersonId(final PersonId personId) {
    return revokeCertificateRepository.findByPersonId(personId.normalized()).stream()
        .map(revokeCertificateConverter::convert)
        .map(this::toRevocation)
        .toList();
  }

  private Revocation toRevocation(final RevokeCertificateDTO dto) {
    final var staffId =
        dto.getSkickadAv() != null && dto.getSkickadAv().getPersonalId() != null
            ? dto.getSkickadAv().getPersonalId().getExtension()
            : null;
    final var staffName =
        dto.getSkickadAv() != null ? dto.getSkickadAv().getFullstandigtNamn() : null;

    return Revocation.builder()
        .certificateId(dto.getIntygsId() != null ? dto.getIntygsId().getExtension() : null)
        .personId(
            dto.getPatientPersonId() != null
                ? PersonId.of(dto.getPatientPersonId().getExtension())
                : null)
        .revokedAt(dto.getSkickatTidpunkt())
        .reason(dto.getMeddelande())
        .revokedByStaffId(staffId)
        .revokedByFullName(staffName)
        .build();
  }
}
