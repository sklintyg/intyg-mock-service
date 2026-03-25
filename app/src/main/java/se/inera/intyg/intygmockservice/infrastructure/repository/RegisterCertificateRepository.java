package se.inera.intyg.intygmockservice.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.infrastructure.config.properties.AppProperties;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

@Repository
public class RegisterCertificateRepository
    extends AbstractInMemoryRepository<RegisterCertificateType> {

  public RegisterCertificateRepository(AppProperties appProperties) {
    super(appProperties.repository().registerCertificate().maxSize());
  }

  public Optional<RegisterCertificateType> findByCertificateId(final String certificateId) {
    return findAll().stream()
        .filter(t -> certificateId.equals(t.getIntyg().getIntygsId().getExtension()))
        .findFirst();
  }

  public List<RegisterCertificateType> findByLogicalAddress(final String logicalAddress) {
    return findByKey(logicalAddress);
  }

  public List<RegisterCertificateType> findByPersonId(final String normalizedPersonId) {
    return findAll().stream()
        .filter(
            t ->
                normalizedPersonId.equals(
                    PersonId.of(t.getIntyg().getPatient().getPersonId().getExtension())
                        .normalized()))
        .toList();
  }

  public void deleteById(final String certificateId) {
    removeIf(t -> certificateId.equals(t.getIntyg().getIntygsId().getExtension()));
  }
}
