package se.inera.intyg.intygmockservice.revokecertificate;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.revokecertificate.converter.RevokeCertificateConverter;
import se.inera.intyg.intygmockservice.revokecertificate.dto.RevokeCertificateDTO;
import se.inera.intyg.intygmockservice.revokecertificate.repository.RevokeCertificateRepository;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevokeCertificateService {

  private final RevokeCertificateRepository repository;
  private final RevokeCertificateConverter converter;

  public void store(final String logicalAddress, final RevokeCertificateType revokeCertificate) {
    repository.add(logicalAddress, revokeCertificate);

    final var dto = converter.convert(revokeCertificate);

    log.atInfo()
        .setMessage(
            "Certificate '%s' revoked with message '%s'"
                .formatted(dto.getIntygsId().getExtension(), dto.getMeddelande()))
        .addKeyValue("event.logical_address", logicalAddress)
        .addKeyValue("event.certificate.id", dto.getIntygsId().getExtension())
        .log();
  }

  public List<RevokeCertificateDTO> getAll() {
    return repository.findAll().stream().map(converter::convert).toList();
  }

  public Optional<RevokeCertificateDTO> getById(final String certificateId) {
    return repository.findByCertificateId(certificateId).map(converter::convert);
  }

  public List<RevokeCertificateDTO> getByLogicalAddress(final String logicalAddress) {
    return repository.findByLogicalAddress(logicalAddress).stream()
        .map(converter::convert)
        .toList();
  }

  public List<RevokeCertificateDTO> getByPersonId(final String personId) {
    final var normalized = normalizePersonId(personId);
    return repository.findByPersonId(normalized).stream().map(converter::convert).toList();
  }

  public int getCount() {
    return repository.count();
  }

  public void deleteAll() {
    repository.deleteAll();
  }

  public void deleteById(final String certificateId) {
    repository.deleteById(certificateId);
  }

  private static String normalizePersonId(final String personId) {
    return personId == null ? null : personId.replace("-", "");
  }
}
