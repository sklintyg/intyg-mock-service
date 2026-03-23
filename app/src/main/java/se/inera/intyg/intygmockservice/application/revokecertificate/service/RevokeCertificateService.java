package se.inera.intyg.intygmockservice.application.revokecertificate.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.revokecertificate.converter.RevokeCertificateConverter;
import se.inera.intyg.intygmockservice.application.revokecertificate.dto.RevokeCertificateDTO;
import se.inera.intyg.intygmockservice.domain.behavior.model.MatchContext;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.RevokeCertificatePassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.BehaviorRuleRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.RevokeCertificateRepository;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.ObjectFactory;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevokeCertificateService {

  private static final JAXBContext JAXB_CONTEXT;

  static {
    try {
      JAXB_CONTEXT =
          JAXBContext.newInstance(
              "se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2"
                  + ":se.riv.clinicalprocess.healthcond.certificate.v3"
                  + ":se.riv.clinicalprocess.healthcond.certificate.types.v3"
                  + ":org.w3._2000._09.xmldsig_"
                  + ":org.w3._2002._06.xmldsig_filter2");
    } catch (JAXBException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private final RevokeCertificateRepository repository;
  private final RevokeCertificateConverter converter;
  private final RevokeCertificatePassthroughClient passthroughClient;
  private final BehaviorRuleRepository behaviorRuleRepository;
  private final RevokeCertificateResponseFactory responseFactory;

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
    return repository.findByCertificateId(certificateId).map(this::marshalToXml);
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

  private String marshalToXml(final RevokeCertificateType type) {
    try {
      final var marshaller = JAXB_CONTEXT.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      final var element = new ObjectFactory().createRevokeCertificate(type);
      final var sw = new StringWriter();
      marshaller.marshal(element, sw);
      return sw.toString();
    } catch (JAXBException e) {
      throw new IllegalStateException("Failed to marshal revocation to XML", e);
    }
  }
}
