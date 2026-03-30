package se.inera.intyg.intygmockservice.application.revokecertificate.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.revokecertificate.converter.RevokeCertificateConverter;
import se.inera.intyg.intygmockservice.application.revokecertificate.dto.RevokeCertificateDTO;
import se.inera.intyg.intygmockservice.domain.behavior.model.MatchContext;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;
import se.inera.intyg.intygmockservice.domain.behavior.repository.BehaviorRuleRepository;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.RevokeCertificatePassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.RevokeCertificateRepository;
import se.inera.intyg.intygmockservice.infrastructure.xml.JaxbXmlMarshaller;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevokeCertificateService {

  private final RevokeCertificateRepository repository;
  private final RevokeCertificateConverter converter;
  private final RevokeCertificatePassthroughClient passthroughClient;
  private final BehaviorRuleRepository behaviorRuleRepository;
  private final RevokeCertificateResponseFactory responseFactory;
  private final JaxbXmlMarshaller xmlMarshaller;

  public Optional<RevokeCertificateResponseType> store(
      final String logicalAddress, final RevokeCertificateType revokeCertificate) {
    final var dto = converter.convert(revokeCertificate);

    final var context =
        MatchContext.builder()
            .logicalAddress(logicalAddress)
            .certificateId(dto.getIntygsId().getExtension())
            .personId(dto.getPatientPersonId().getExtension())
            .build();

    final var ruleOpt =
        behaviorRuleRepository.findBestMatch(ServiceName.REVOKE_CERTIFICATE, context);

    if (ruleOpt.isPresent()) {
      final var resultOpt = ruleOpt.get().evaluate(context);
      if (resultOpt.isPresent()) {
        return Optional.of(responseFactory.create(resultOpt.get()));
      }
    }

    repository.add(logicalAddress, revokeCertificate);

    log.atInfo()
        .setMessage(
            "Certificate '%s' revoked with message '%s'"
                .formatted(dto.getIntygsId().getExtension(), dto.getMeddelande()))
        .addKeyValue("event.logical_address", logicalAddress)
        .addKeyValue("event.certificate.id", dto.getIntygsId().getExtension())
        .log();

    return passthroughClient.forward(logicalAddress, revokeCertificate);
  }

  public List<RevokeCertificateDTO> getAll() {
    return repository.findAll().stream().map(converter::convert).toList();
  }

  public Optional<RevokeCertificateDTO> getById(final String certificateId) {
    return repository.findByCertificateId(certificateId).map(converter::convert);
  }

  public Optional<String> getAsXml(final String certificateId) {
    return repository.findByCertificateId(certificateId).map(xmlMarshaller::marshal);
  }

  public List<RevokeCertificateDTO> getByLogicalAddress(final String logicalAddress) {
    return repository.findByLogicalAddress(logicalAddress).stream()
        .map(converter::convert)
        .toList();
  }

  public List<RevokeCertificateDTO> getByPersonId(final String personId) {
    return repository.findByPersonId(PersonId.of(personId).normalized()).stream()
        .map(converter::convert)
        .toList();
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
}
