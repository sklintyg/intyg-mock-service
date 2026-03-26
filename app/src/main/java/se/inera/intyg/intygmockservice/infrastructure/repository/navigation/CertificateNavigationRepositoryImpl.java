package se.inera.intyg.intygmockservice.infrastructure.repository.navigation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.application.registercertificate.converter.RegisterCertificateConverter;
import se.inera.intyg.intygmockservice.domain.navigation.model.CareProvider;
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.domain.navigation.model.Staff;
import se.inera.intyg.intygmockservice.domain.navigation.model.Unit;
import se.inera.intyg.intygmockservice.domain.navigation.repository.CertificateNavigationRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.CertificateStatusUpdateForCareRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.RegisterCertificateRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.RevokeCertificateRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.SendMessageToRecipientRepository;
import se.inera.intyg.intygmockservice.infrastructure.repository.StoreLogTypeRepository;

@Repository
@RequiredArgsConstructor
public class CertificateNavigationRepositoryImpl implements CertificateNavigationRepository {

  private final RegisterCertificateRepository registerCertificateRepository;
  private final RevokeCertificateRepository revokeCertificateRepository;
  private final SendMessageToRecipientRepository sendMessageToRecipientRepository;
  private final CertificateStatusUpdateForCareRepository statusUpdateRepository;
  private final StoreLogTypeRepository storeLogTypeRepository;
  private final RegisterCertificateConverter registerCertificateConverter;

  @Override
  public List<Certificate> findAll() {
    return buildMergedMap().values().stream().toList();
  }

  @Override
  public Optional<Certificate> findById(final String certificateId) {
    return buildMergedMap().values().stream()
        .filter(c -> certificateId.equals(c.getCertificateId()))
        .findFirst();
  }

  @Override
  public List<Certificate> findByPersonId(final String normalizedPersonId) {
    return buildMergedMap().values().stream()
        .filter(
            c ->
                c.getPatient() != null
                    && c.getPatient().getPersonId() != null
                    && normalizedPersonId.equals(c.getPatient().getPersonId().normalized()))
        .toList();
  }

  @Override
  public List<Certificate> findByUnitId(final String unitId) {
    return buildMergedMap().values().stream()
        .filter(
            c ->
                c.getIssuedBy() != null
                    && c.getIssuedBy().getUnit() != null
                    && unitId.equals(c.getIssuedBy().getUnit().getUnitId()))
        .toList();
  }

  @Override
  public List<Certificate> findByStaffId(final String staffId) {
    return buildMergedMap().values().stream()
        .filter(c -> c.getIssuedBy() != null && staffId.equals(c.getIssuedBy().getStaffId()))
        .toList();
  }

  /**
   * Builds a merged map of certificateId → Certificate. RegisterCertificate is the richest source;
   * other services contribute certificate IDs (and personIds) for certificates not seen via
   * registration.
   */
  private Map<String, Certificate> buildMergedMap() {
    final Map<String, Certificate> map = new LinkedHashMap<>();

    // Richest source first — full patient, staff, unit data available
    registerCertificateRepository.findAll().stream()
        .map(registerCertificateConverter::convert)
        .forEach(
            dto -> {
              final var certId = dto.getIntyg().getIntygsId().getExtension();
              map.put(certId, toCertificate(dto));
            });

    // Revocations — add stub for certificate IDs not yet registered
    revokeCertificateRepository.findAll().stream()
        .filter(r -> !map.containsKey(r.getIntygsId().getExtension()))
        .forEach(
            r -> {
              final var certId = r.getIntygsId().getExtension();
              final var personId = PersonId.of(r.getPatientPersonId().getExtension());
              map.put(
                  certId,
                  Certificate.builder()
                      .certificateId(certId)
                      .patient(Patient.builder().personId(personId).build())
                      .build());
            });

    // Messages — add stub for certificate IDs not yet seen
    sendMessageToRecipientRepository.findAll().stream()
        .filter(m -> !map.containsKey(m.getIntygsId().getExtension()))
        .forEach(
            m -> {
              final var certId = m.getIntygsId().getExtension();
              final var personId = PersonId.of(m.getPatientPersonId().getExtension());
              map.put(
                  certId,
                  Certificate.builder()
                      .certificateId(certId)
                      .patient(Patient.builder().personId(personId).build())
                      .build());
            });

    // Status updates — add stub for certificate IDs not yet seen
    statusUpdateRepository.findAll().stream()
        .filter(s -> !map.containsKey(s.getIntyg().getIntygsId().getExtension()))
        .forEach(
            s -> {
              final var certId = s.getIntyg().getIntygsId().getExtension();
              final var personId =
                  PersonId.of(s.getIntyg().getPatient().getPersonId().getExtension());
              map.put(
                  certId,
                  Certificate.builder()
                      .certificateId(certId)
                      .patient(Patient.builder().personId(personId).build())
                      .build());
            });

    // Store log — activity level is the certificate ID (single-char values 1-3 are level codes,
    // not cert IDs); personId from first resource patient
    storeLogTypeRepository.findAll().stream()
        .flatMap(storeLog -> storeLog.getLog().stream())
        .filter(
            log -> {
              final var certId = log.getActivity().getActivityLevel();
              return certId != null && certId.length() > 1 && !map.containsKey(certId);
            })
        .forEach(
            log -> {
              final var certId = log.getActivity().getActivityLevel();
              final var personId =
                  log.getResources() != null
                      ? log.getResources().getResource().stream()
                          .filter(r -> r.getPatient() != null)
                          .findFirst()
                          .map(r -> PersonId.of(r.getPatient().getPatientId().getExtension()))
                          .orElse(null)
                      : null;
              map.put(
                  certId,
                  Certificate.builder()
                      .certificateId(certId)
                      .patient(
                          personId != null ? Patient.builder().personId(personId).build() : null)
                      .build());
            });

    return map;
  }

  private Certificate toCertificate(
      final se.inera.intyg.intygmockservice.application.registercertificate.dto
              .RegisterCertificateDTO
          dto) {
    final var intyg = dto.getIntyg();
    return Certificate.builder()
        .certificateId(intyg.getIntygsId().getExtension())
        .certificateType(intyg.getTyp() != null ? intyg.getTyp().getCode() : null)
        .certificateTypeDisplayName(intyg.getTyp() != null ? intyg.getTyp().getDisplayName() : null)
        .signingTimestamp(intyg.getSigneringstidpunkt())
        .sentTimestamp(intyg.getSkickatTidpunkt())
        .version(intyg.getVersion())
        .patient(toPatient(intyg.getPatient()))
        .issuedBy(toStaff(intyg.getSkapadAv()))
        .build();
  }

  private Patient toPatient(
      final se.inera.intyg.intygmockservice.application.common.dto.PatientDTO dto) {
    if (dto == null) {
      return null;
    }
    return Patient.builder()
        .personId(dto.getPersonId() != null ? PersonId.of(dto.getPersonId().getExtension()) : null)
        .firstName(dto.getFornamn())
        .lastName(dto.getEfternamn())
        .streetAddress(dto.getPostadress())
        .postalCode(dto.getPostnummer())
        .city(dto.getPostort())
        .build();
  }

  private Staff toStaff(
      final se.inera.intyg.intygmockservice.application.common.dto.HoSPersonalDTO dto) {
    if (dto == null) {
      return null;
    }
    return Staff.builder()
        .staffId(dto.getPersonalId() != null ? dto.getPersonalId().getExtension() : null)
        .fullName(dto.getFullstandigtNamn())
        .prescriptionCode(dto.getForskrivarkod())
        .unit(toUnit(dto.getEnhet()))
        .build();
  }

  private Unit toUnit(
      final se.inera.intyg.intygmockservice.application.common.dto.HoSPersonalDTO.EnhetDTO dto) {
    if (dto == null) {
      return null;
    }
    return Unit.builder()
        .unitId(dto.getEnhetsId() != null ? dto.getEnhetsId().getExtension() : null)
        .unitName(dto.getEnhetsnamn())
        .streetAddress(dto.getPostadress())
        .postalCode(dto.getPostnummer())
        .city(dto.getPostort())
        .phone(dto.getTelefonnummer())
        .email(dto.getEpost())
        .careProvider(toCareProvider(dto.getVardgivare()))
        .build();
  }

  private CareProvider toCareProvider(
      final se.inera.intyg.intygmockservice.application.common.dto.HoSPersonalDTO.EnhetDTO
              .VardgivareDTO
          dto) {
    if (dto == null) {
      return null;
    }
    return CareProvider.builder()
        .careProviderId(dto.getVardgivareId() != null ? dto.getVardgivareId().getExtension() : null)
        .careProviderName(dto.getVardgivarnamn())
        .build();
  }
}
