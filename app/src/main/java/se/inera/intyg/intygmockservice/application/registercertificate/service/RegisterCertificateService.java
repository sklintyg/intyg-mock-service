package se.inera.intyg.intygmockservice.application.registercertificate.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.registercertificate.converter.RegisterCertificateConverter;
import se.inera.intyg.intygmockservice.application.registercertificate.dto.RegisterCertificateDTO;
import se.inera.intyg.intygmockservice.domain.behavior.model.MatchContext;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;
import se.inera.intyg.intygmockservice.domain.behavior.repository.BehaviorRuleRepository;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.RegisterCertificatePassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.RegisterCertificateRepository;
import se.inera.intyg.intygmockservice.infrastructure.xml.JaxbXmlMarshaller;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterCertificateService {

  private final RegisterCertificateRepository repository;
  private final RegisterCertificateConverter converter;
  private final RegisterCertificatePassthroughClient passthroughClient;
  private final BehaviorRuleRepository behaviorRuleRepository;
  private final RegisterCertificateResponseFactory responseFactory;
  private final JaxbXmlMarshaller xmlMarshaller;

  public Optional<RegisterCertificateResponseType> store(
      String logicalAddress, RegisterCertificateType type) {
    final var dto = converter.convert(type);

    final var context =
        MatchContext.builder()
            .logicalAddress(logicalAddress)
            .certificateId(dto.getIntyg().getIntygsId().getExtension())
            .personId(dto.getIntyg().getPatient().getPersonId().getExtension())
            .build();

    final var ruleOpt =
        behaviorRuleRepository.findBestMatch(ServiceName.REGISTER_CERTIFICATE, context);

    if (ruleOpt.isPresent()) {
      final var resultOpt = ruleOpt.get().evaluate(context);
      if (resultOpt.isPresent()) {
        return Optional.of(responseFactory.create(resultOpt.get()));
      }
    }

    repository.add(logicalAddress, type);

    log.atInfo()
        .setMessage(
            "Register certificate '%s' received"
                .formatted(dto.getIntyg().getIntygsId().getExtension()))
        .addKeyValue("event.logical_address", logicalAddress)
        .addKeyValue("event.certificate.id", dto.getIntyg().getIntygsId().getExtension())
        .addKeyValue(
            "event.answered.message.id",
            dto.getSvarPa() != null ? dto.getSvarPa().getMeddelandeId() : "-")
        .log();

    return passthroughClient.forward(logicalAddress, type);
  }

  public List<RegisterCertificateDTO> getAll() {
    return repository.findAll().stream().map(converter::convert).toList();
  }

  public Optional<RegisterCertificateDTO> getById(final String certificateId) {
    return repository.findByCertificateId(certificateId).map(converter::convert);
  }

  public Optional<String> getAsXml(final String certificateId) {
    return repository.findByCertificateId(certificateId).map(xmlMarshaller::marshal);
  }

  public List<RegisterCertificateDTO> getByLogicalAddress(final String logicalAddress) {
    return repository.findByLogicalAddress(logicalAddress).stream()
        .map(converter::convert)
        .toList();
  }

  public List<RegisterCertificateDTO> getByPersonId(final String personId) {
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
