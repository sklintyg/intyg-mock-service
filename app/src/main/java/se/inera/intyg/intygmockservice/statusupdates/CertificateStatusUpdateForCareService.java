package se.inera.intyg.intygmockservice.statusupdates;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.statusupdates.converter.CertificateStatusUpdateForCareConverter;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO;
import se.inera.intyg.intygmockservice.statusupdates.repository.CertificateStatusUpdateForCareRepository;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateStatusUpdateForCareService {

  private final CertificateStatusUpdateForCareRepository repository;
  private final CertificateStatusUpdateForCareConverter converter;

  public void store(final String logicalAddress, final CertificateStatusUpdateForCareType request) {
    repository.add(logicalAddress, request);

    final var dto = converter.convert(request);

    log.atInfo()
        .setMessage(
            "Certificate '%s' received status update of type '%s'"
                .formatted(
                    dto.getIntyg().getIntygsId().getExtension(),
                    dto.getHandelse().getHandelsekod().getCode()))
        .addKeyValue("event.logical_address", logicalAddress)
        .addKeyValue("event.certificate.id", dto.getIntyg().getIntygsId().getExtension())
        .addKeyValue("event.type", dto.getHandelse().getHandelsekod().getCode())
        .addKeyValue(
            "event.handled_by",
            dto.getHanteratAv() != null ? dto.getHanteratAv().getExtension() : null)
        .log();
  }

  public List<CertificateStatusUpdateForCareDTO> getAll() {
    return repository.findAll().stream().map(converter::convert).toList();
  }

  public List<CertificateStatusUpdateForCareDTO> getByCertificateId(final String certificateId) {
    return repository.findByCertificateId(certificateId).stream().map(converter::convert).toList();
  }

  public List<CertificateStatusUpdateForCareDTO> getByLogicalAddress(final String logicalAddress) {
    return repository.findByLogicalAddress(logicalAddress).stream()
        .map(converter::convert)
        .toList();
  }

  public List<CertificateStatusUpdateForCareDTO> getByPersonId(final String personId) {
    final var normalized = normalizePersonId(personId);
    return repository.findByPersonId(normalized).stream().map(converter::convert).toList();
  }

  public List<CertificateStatusUpdateForCareDTO> getByEventCode(final String eventCode) {
    return repository.findByEventCode(eventCode).stream().map(converter::convert).toList();
  }

  public int getCount() {
    return repository.count();
  }

  public void deleteAll() {
    repository.deleteAll();
  }

  public void deleteByCertificateId(final String certificateId) {
    repository.deleteByCertificateId(certificateId);
  }

  private static String normalizePersonId(final String personId) {
    return personId == null ? null : personId.replace("-", "");
  }
}
