package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.model.StatusUpdate;
import se.inera.intyg.intygmockservice.domain.navigation.repository.StatusUpdateNavigationRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.CertificateStatusUpdateForCareRepository;
import se.inera.intyg.intygmockservice.infrastructure.xml.JaxbXmlMarshaller;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;

@Repository
@RequiredArgsConstructor
public class StatusUpdateNavigationRepositoryImpl implements StatusUpdateNavigationRepository {

  private final CertificateStatusUpdateForCareRepository statusUpdateRepository;
  private final JaxbXmlMarshaller xmlMarshaller;

  @Override
  public List<StatusUpdate> findAll() {
    return statusUpdateRepository.findAll().stream().map(this::toStatusUpdate).toList();
  }

  @Override
  public List<StatusUpdate> findByCertificateId(final String certificateId) {
    return statusUpdateRepository.findByCertificateId(certificateId).stream()
        .map(this::toStatusUpdate)
        .toList();
  }

  @Override
  public List<StatusUpdate> findByPersonId(final PersonId personId) {
    return statusUpdateRepository.findByPersonId(personId.normalized()).stream()
        .map(this::toStatusUpdate)
        .toList();
  }

  private StatusUpdate toStatusUpdate(final CertificateStatusUpdateForCareType source) {
    final var intyg = source.getIntyg();
    final var handelse = source.getHandelse();

    final var certificateId =
        intyg != null && intyg.getIntygsId() != null ? intyg.getIntygsId().getExtension() : null;
    final var personId =
        intyg != null && intyg.getPatient() != null && intyg.getPatient().getPersonId() != null
            ? PersonId.of(intyg.getPatient().getPersonId().getExtension())
            : null;
    final var eventCode =
        handelse != null && handelse.getHandelsekod() != null
            ? handelse.getHandelsekod().getCode()
            : null;
    final var eventDisplayName =
        handelse != null && handelse.getHandelsekod() != null
            ? handelse.getHandelsekod().getDisplayName()
            : null;
    final var eventTimestamp = handelse != null ? handelse.getTidpunkt() : null;

    final var sentTotal =
        source.getSkickadeFragor() != null ? source.getSkickadeFragor().getTotalt() : 0;
    final var receivedTotal =
        source.getMottagnaFragor() != null ? source.getMottagnaFragor().getTotalt() : 0;

    return StatusUpdate.builder()
        .certificateId(certificateId)
        .personId(personId)
        .eventCode(eventCode)
        .eventDisplayName(eventDisplayName)
        .eventTimestamp(eventTimestamp)
        .questionsSentTotal(sentTotal)
        .questionsReceivedTotal(receivedTotal)
        .sourceXml(xmlMarshaller.marshal(source))
        .build();
  }
}
