package se.inera.intyg.intygmockservice.application.statusupdates.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.statusupdates.converter.CertificateStatusUpdateForCareConverter;
import se.inera.intyg.intygmockservice.application.statusupdates.dto.CertificateStatusUpdateForCareDTO;
import se.inera.intyg.intygmockservice.domain.behavior.model.MatchContext;
import se.inera.intyg.intygmockservice.domain.behavior.model.ServiceName;
import se.inera.intyg.intygmockservice.domain.behavior.repository.BehaviorRuleRepository;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.CertificateStatusUpdateForCarePassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.CertificateStatusUpdateForCareRepository;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponseType;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.ObjectFactory;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateStatusUpdateForCareService {

  private static final JAXBContext JAXB_CONTEXT;

  static {
    try {
      JAXB_CONTEXT =
          JAXBContext.newInstance(
              "se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3"
                  + ":se.riv.clinicalprocess.healthcond.certificate.v3"
                  + ":se.riv.clinicalprocess.healthcond.certificate.types.v3"
                  + ":org.w3._2000._09.xmldsig_"
                  + ":org.w3._2002._06.xmldsig_filter2");
    } catch (JAXBException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

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
    return repository.findByPersonId(PersonId.of(personId).normalized()).stream()
        .map(converter::convert)
        .toList();
  }

  public List<CertificateStatusUpdateForCareDTO> getByEventCode(final String eventCode) {
    return repository.findByEventCode(eventCode).stream().map(converter::convert).toList();
  }

  public int getCount() {
    return repository.count();
  }

  public Optional<String> getAsXmlByCertificateId(final String certificateId) {
    final var types = repository.findByCertificateId(certificateId);
    if (types.isEmpty()) {
      return Optional.empty();
    }
    final var sb =
        new StringBuilder(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<status-updates>\n");
    for (final var type : types) {
      sb.append(marshalFragmentToXml(type)).append("\n");
    }
    sb.append("</status-updates>");
    return Optional.of(sb.toString());
  }

  public void deleteAll() {
    repository.deleteAll();
  }

  public void deleteByCertificateId(final String certificateId) {
    repository.deleteByCertificateId(certificateId);
  }

  private String marshalFragmentToXml(final CertificateStatusUpdateForCareType type) {
    try {
      final var marshaller = JAXB_CONTEXT.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
      final var element = new ObjectFactory().createCertificateStatusUpdateForCare(type);
      final var sw = new StringWriter();
      marshaller.marshal(element, sw);
      return sw.toString();
    } catch (JAXBException e) {
      throw new IllegalStateException("Failed to marshal status update to XML", e);
    }
  }
}
