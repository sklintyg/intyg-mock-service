package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.application.statusupdates.converter.CertificateStatusUpdateForCareConverter;
import se.inera.intyg.intygmockservice.application.statusupdates.dto.CertificateStatusUpdateForCareDTO;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.model.StatusUpdate;
import se.inera.intyg.intygmockservice.domain.navigation.repository.StatusUpdateNavigationRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.CertificateStatusUpdateForCareRepository;

@Repository
@RequiredArgsConstructor
public class StatusUpdateNavigationRepositoryImpl implements StatusUpdateNavigationRepository {

  private final CertificateStatusUpdateForCareRepository statusUpdateRepository;
  private final CertificateStatusUpdateForCareConverter converter;

  @Override
  public List<StatusUpdate> findAll() {
    return statusUpdateRepository.findAll().stream()
        .map(converter::convert)
        .map(this::toStatusUpdate)
        .toList();
  }

  @Override
  public List<StatusUpdate> findByCertificateId(final String certificateId) {
    return statusUpdateRepository.findByCertificateId(certificateId).stream()
        .map(converter::convert)
        .map(this::toStatusUpdate)
        .toList();
  }

  @Override
  public List<StatusUpdate> findByPersonId(final String normalizedPersonId) {
    return statusUpdateRepository.findByPersonId(normalizedPersonId).stream()
        .map(converter::convert)
        .map(this::toStatusUpdate)
        .toList();
  }

  private StatusUpdate toStatusUpdate(final CertificateStatusUpdateForCareDTO dto) {
    final var intyg = dto.getIntyg();
    final var handelse = dto.getHandelse();

    final var certificateId =
        intyg != null && intyg.getIntygsId() != null ? intyg.getIntygsId().getExtension() : null;
    final var personId =
        intyg != null && intyg.getPatient() != null && intyg.getPatient().getPersonId() != null
            ? PersonId.of(intyg.getPatient().getPersonId().getExtension()).normalized()
            : null;
    final var eventCode =
        handelse != null && handelse.getHandelsekod() != null
            ? handelse.getHandelsekod().getCode()
            : null;
    final var eventDisplayName =
        handelse != null && handelse.getHandelsekod() != null
            ? handelse.getHandelsekod().getDisplayName()
            : null;
    final var eventTimestamp = handelse != null ? parseTidpunkt(handelse.getTidpunkt()) : null;

    final var sentTotal = dto.getSkickadeFragor() != null ? dto.getSkickadeFragor().getTotalt() : 0;
    final var receivedTotal =
        dto.getMottagnaFragor() != null ? dto.getMottagnaFragor().getTotalt() : 0;

    return StatusUpdate.builder()
        .certificateId(certificateId)
        .personId(personId)
        .eventCode(eventCode)
        .eventDisplayName(eventDisplayName)
        .eventTimestamp(eventTimestamp)
        .questionsSentTotal(sentTotal)
        .questionsReceivedTotal(receivedTotal)
        .build();
  }

  private static LocalDateTime parseTidpunkt(final String tidpunkt) {
    if (tidpunkt == null) {
      return null;
    }
    try {
      return LocalDateTime.parse(tidpunkt);
    } catch (DateTimeParseException e) {
      return OffsetDateTime.parse(tidpunkt).toLocalDateTime();
    }
  }
}
