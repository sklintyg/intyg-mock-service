package se.inera.intyg.intygmockservice.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.infrastructure.config.properties.AppProperties;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;

@Repository
public class RevokeCertificateRepository extends AbstractInMemoryRepository<RevokeCertificateType> {

  public RevokeCertificateRepository(AppProperties appProperties) {
    super(appProperties.repository().revokeCertificate().maxSize());
  }

  public Optional<RevokeCertificateType> findByCertificateId(final String certificateId) {
    return findAll().stream()
        .filter(t -> certificateId.equals(t.getIntygsId().getExtension()))
        .findFirst();
  }

  public List<RevokeCertificateType> findByLogicalAddress(final String logicalAddress) {
    return findByKey(logicalAddress);
  }

  public List<RevokeCertificateType> findByPersonId(final String normalizedPersonId) {
    return findAll().stream()
        .filter(t -> normalizedPersonId.equals(normalize(t.getPatientPersonId().getExtension())))
        .toList();
  }

  public void deleteById(final String certificateId) {
    removeIf(t -> certificateId.equals(t.getIntygsId().getExtension()));
  }

  private static String normalize(final String personId) {
    return personId == null ? null : personId.replace("-", "");
  }
}
