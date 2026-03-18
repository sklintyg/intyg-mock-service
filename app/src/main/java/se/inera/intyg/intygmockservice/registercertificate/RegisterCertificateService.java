package se.inera.intyg.intygmockservice.registercertificate;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.registercertificate.converter.RegisterCertificateConverter;
import se.inera.intyg.intygmockservice.registercertificate.dto.RegisterCertificateDTO;
import se.inera.intyg.intygmockservice.registercertificate.passthrough.RegisterCertificatePassthroughClient;
import se.inera.intyg.intygmockservice.registercertificate.repository.RegisterCertificateRepository;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.ObjectFactory;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateResponseType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterCertificateService {

  private final RegisterCertificateRepository repository;
  private final RegisterCertificateConverter converter;
  private final RegisterCertificatePassthroughClient passthroughClient;

  private static final JAXBContext JAXB_CONTEXT;

  static {
    try {
      JAXB_CONTEXT =
          JAXBContext.newInstance(
              "se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3"
                  + ":se.riv.clinicalprocess.healthcond.certificate.v3"
                  + ":se.riv.clinicalprocess.healthcond.certificate.types.v3"
                  + ":org.w3._2000._09.xmldsig_"
                  + ":org.w3._2002._06.xmldsig_filter2");
    } catch (JAXBException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public Optional<RegisterCertificateResponseType> store(
      String logicalAddress, RegisterCertificateType type) {
    repository.add(logicalAddress, type);

    final var dto = converter.convert(type);
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
    return repository.findByCertificateId(certificateId).map(this::marshalToXml);
  }

  public List<RegisterCertificateDTO> getByLogicalAddress(final String logicalAddress) {
    return repository.findByLogicalAddress(logicalAddress).stream()
        .map(converter::convert)
        .toList();
  }

  public List<RegisterCertificateDTO> getByPersonId(final String personId) {
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

  private String marshalToXml(final RegisterCertificateType type) {
    try {
      final var marshaller = JAXB_CONTEXT.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      final var element = new ObjectFactory().createRegisterCertificate(type);
      final var sw = new StringWriter();
      marshaller.marshal(element, sw);
      return sw.toString();
    } catch (JAXBException e) {
      throw new IllegalStateException("Failed to marshal certificate to XML", e);
    }
  }
}
