package se.inera.intyg.intygmockservice.application.statusupdates;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.statusupdates.converter.CertificateStatusUpdateForCareConverter;
import se.inera.intyg.intygmockservice.application.statusupdates.dto.CertificateStatusUpdateForCareDTO;
import se.inera.intyg.intygmockservice.application.statusupdates.service.CertificateStatusUpdateForCareResponseFactory;
import se.inera.intyg.intygmockservice.domain.MatchContext;
import se.inera.intyg.intygmockservice.domain.ServiceName;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.CertificateStatusUpdateForCarePassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.BehaviorRuleRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.CertificateStatusUpdateForCareRepository;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponseType;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateStatusUpdateForCareService {

  private final CertificateStatusUpdateForCareRepository repository;
  private final CertificateStatusUpdateForCareConverter converter;
  private final CertificateStatusUpdateForCarePassthroughClient passthroughClient;
  private final BehaviorRuleRepository behaviorRuleRepository;
  private final CertificateStatusUpdateForCareResponseFactory responseFactory;

  public Optional<CertificateStatusUpdateForCareResponseType> store(
      final String logicalAddress, final CertificateStatusUpdateForCareType request) {
    final var dto = converter.convert(request);

    final var context =
        MatchContext.builder()
            .logicalAddress(logicalAddress)
            .certificateId(dto.getIntyg().getIntygsId().getExtension())
            .personId(dto.getIntyg().getPatient().getPersonId().getExtension())
            .build();

    final var ruleOpt =
        behaviorRuleRepository.findBestMatch(
            ServiceName.CERTIFICATE_STATUS_UPDATE_FOR_CARE, context);

    if (ruleOpt.isPresent()) {
      final var resultOpt = ruleOpt.get().evaluate(context);
      if (resultOpt.isPresent()) {
        return Optional.of(responseFactory.create(resultOpt.get()));
      }
    }

    repository.add(logicalAddress, request);

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

    return passthroughClient.forward(logicalAddress, request);
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
